import copy
import json
import struct
import subprocess
import zlib
import sys
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / 'tools'))
import migrate_languages
import migrate_textures
import recipe_parity


class LanguageConversionTests(unittest.TestCase):
    def test_bom_comments_equals_and_significant_spaces(self):
        source = '\ufeff# comment\n\nfoo= Elixir of \nbar=a=b\n'.encode('utf-8')
        self.assertEqual(migrate_languages.parse_lang(source), {'foo': ' Elixir of ', 'bar': 'a=b'})

    def test_rejects_duplicates_and_invalid_lines(self):
        for content in (b'a=one\na=two\n', b'not a valid line\n', b' bad =value\n'):
            with self.subTest(content=content), self.assertRaises(ValueError):
                migrate_languages.parse_lang(content)

    def test_all_converted_locales_equal_original(self):
        expected = migrate_languages.conversions(migrate_languages.SOURCE)
        self.assertEqual(len(expected), 8)
        for name, data in expected.items():
            with self.subTest(locale=name):
                path = migrate_languages.DESTINATION / name
                self.assertEqual(path.read_bytes(), data)
                self.assertIsInstance(json.loads(data), dict)
        for source in migrate_languages.SOURCE.glob('*.lang'):
            values = json.loads(expected[source.stem.lower() + '.json'])
            original = migrate_languages.parse_lang(source.read_bytes(), source.name)
            with self.subTest(source=source.name):
                for key, value in original.items():
                    self.assertEqual(values[key], value)
                self.assertEqual(values['item.rustic.beeswax'], original['item.rustic.beeswax.name'])
        en = json.loads(expected['en_us.json'])
        self.assertEqual(en['rustic.elixir.prefix'], 'Elixir of ')
        self.assertEqual(en['rustic.elixir.separator'], ', ')
        self.assertIn('fr_fr.json', expected)

    def test_refuses_to_overwrite_and_preflights_all_files(self):
        with tempfile.TemporaryDirectory() as directory:
            base = Path(directory)
            source, dest = base / 'source', base / 'dest'
            source.mkdir()
            dest.mkdir()
            (source / 'en_us.lang').write_text('a=one\n', encoding='utf-8')
            (source / 'es_es.lang').write_text('a=dos\n', encoding='utf-8')
            (dest / 'es_es.json').write_text('custom edit', encoding='utf-8')
            with self.assertRaises(ValueError):
                migrate_languages.convert(source, dest, write=True)
            self.assertEqual((dest / 'es_es.json').read_text(), 'custom edit')
            self.assertFalse((dest / 'en_us.json').exists())
            (dest / 'es_es.json').unlink()
            self.assertEqual(migrate_languages.convert(source, dest, write=True), (2, 2))
            self.assertEqual(migrate_languages.convert(source, dest, write=True), (2, 0))
            self.assertEqual(migrate_languages.convert(source, dest), (2, 0))

    def test_refuses_symlinked_output_file_or_directory(self):
        with tempfile.TemporaryDirectory() as directory:
            base = Path(directory)
            source, dest, other = base / 'source', base / 'dest', base / 'other'
            source.mkdir()
            other.mkdir()
            (source / 'en_us.lang').write_text('a=b\n')
            dest.symlink_to(other, target_is_directory=True)
            with self.assertRaises(ValueError):
                migrate_languages.convert(source, dest, write=True)
            self.assertFalse((other / 'en_us.json').exists())
            dest.unlink()
            dest.mkdir()
            (dest / 'en_us.json').symlink_to(other / 'en_us.json')
            with self.assertRaises(ValueError):
                migrate_languages.convert(source, dest, write=True)
            self.assertFalse((other / 'en_us.json').exists())

    def test_case_insensitive_locale_collision_rejected(self):
        with tempfile.TemporaryDirectory() as directory:
            base = Path(directory)
            (base / 'fr_FR.lang').write_text('a=b\n')
            (base / 'fr_fr.lang').write_text('a=b\n')
            with self.assertRaises(ValueError):
                migrate_languages.conversions(base)


class TextureCopyTests(unittest.TestCase):
    def test_all_copied_files_equal_original(self):
        self.assertEqual(migrate_textures.copy_textures(migrate_textures.SOURCE, migrate_textures.DESTINATION), (376, 0))
        originals = [p for p in migrate_textures.SOURCE.rglob('*') if p.is_file()]
        self.assertEqual(sum(p.suffix == '.png' for p in originals), 343)
        self.assertEqual(sum(p.suffix == '.mcmeta' for p in originals), 32)

    def test_png_chunks_and_animation_metadata_are_well_formed(self):
        root = migrate_textures.DESTINATION
        for path in root.rglob('*.png'):
            with self.subTest(path=path.name):
                data = path.read_bytes()
                self.assertEqual(data[:8], b'\x89PNG\r\n\x1a\n')
                offset, chunks = 8, []
                while offset < len(data):
                    self.assertGreaterEqual(len(data) - offset, 12)
                    length = struct.unpack_from('>I', data, offset)[0]
                    self.assertLessEqual(offset + length + 12, len(data))
                    kind = data[offset + 4:offset + 8]
                    body = data[offset + 8:offset + 8 + length]
                    checksum = struct.unpack_from('>I', data, offset + 8 + length)[0]
                    self.assertEqual(checksum, zlib.crc32(kind + body))
                    chunks.append(kind)
                    offset += 12 + length
                    if kind == b'IEND':
                        break
                self.assertEqual(chunks[0], b'IHDR')
                self.assertEqual(chunks[-1], b'IEND')
                self.assertEqual(offset, len(data))
        for path in root.rglob('*.mcmeta'):
            with self.subTest(path=path.name):
                self.assertIsInstance(json.loads(path.read_text(encoding='utf-8')), dict)

    def test_preflight_refuses_different_files_and_symlinked_directories(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            source, dest, other = root / 'source', root / 'dest', root / 'other'
            (source / 'nested').mkdir(parents=True)
            dest.mkdir()
            other.mkdir()
            (source / 'nested/file.png').write_bytes(b'original')
            (source / 'second.png').write_bytes(b'original2')
            (dest / 'nested').symlink_to(other, target_is_directory=True)
            with self.assertRaises(ValueError):
                migrate_textures.copy_textures(source, dest, write=True)
            self.assertFalse((dest / 'second.png').exists())
            (dest / 'nested').unlink()
            (dest / 'nested').mkdir()
            (dest / 'nested/file.png').write_bytes(b'changed')
            with self.assertRaises(ValueError):
                migrate_textures.copy_textures(source, dest, write=True)
            self.assertEqual((dest / 'nested/file.png').read_bytes(), b'changed')
            self.assertFalse((dest / 'second.png').exists())
            (dest / 'nested/file.png').unlink()
            self.assertEqual(migrate_textures.copy_textures(source, dest, write=True), (2, 2))
            self.assertEqual(migrate_textures.copy_textures(source, dest), (2, 0))


class RecipeParityTests(unittest.TestCase):
    def setUp(self):
        self.manifest = recipe_parity.new_manifest()
        self.expected = recipe_parity.expected_entries()

    def test_full_source_index_is_pending_and_cli_fails_closed(self):
        self.assertEqual(len(self.expected), 165)
        errors, pending = recipe_parity.validate(self.manifest, self.expected)
        self.assertEqual(errors, [])
        self.assertEqual(pending, 169)
        result = subprocess.run([sys.executable, str(ROOT / 'tools/recipe_parity.py'), 'check'],
                                cwd=ROOT, text=True, capture_output=True)
        self.assertEqual(result.returncode, 1, result.stdout + result.stderr)
        self.assertIn('Pendientes: 169', result.stdout)

    def test_missing_duplicate_unknown_and_changed_entries_fail(self):
        altered = copy.deepcopy(self.manifest)
        first = altered['entries'][0]
        first['file_sha256'] = 'not-the-source-hash'
        altered['entries'].append(copy.deepcopy(first))
        altered['entries'].pop(1)
        altered['entries'].append({'id': 'json:invented'})
        errors, _ = recipe_parity.validate(altered, self.expected)
        combined = '\n'.join(errors)
        self.assertIn('mismatched file_sha256', combined)
        self.assertIn('duplicate entry:', combined)
        self.assertIn('missing entry:', combined)
        self.assertIn('unknown entry:', combined)

    def test_verified_requires_files_and_coverage_cannot_be_removed(self):
        altered = copy.deepcopy(self.manifest)
        altered['entries'][0]['status'] = 'verified'
        altered['coverage'].pop()
        errors, _ = recipe_parity.validate(altered, self.expected)
        self.assertTrue(any('verified requires targets' in e for e in errors))
        self.assertTrue(any('verified requires evidence' in e for e in errors))
        self.assertTrue(any('missing coverage:' in e for e in errors))

    def test_paths_must_not_escape_workspace_or_target_reference_copy(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / 'src').mkdir()
            (root / 'original').mkdir()
            (root / 'src/recipe.json').write_text('{}')
            (root / 'original/legacy.json').write_text('{}')
            (root / 'src/link.json').symlink_to(root / 'original/legacy.json')
            self.assertTrue(recipe_parity.safe_file(root, 'src/recipe.json', target=True))
            for path in ('../out', '/etc/passwd', 'original/legacy.json', 'src/link.json'):
                self.assertFalse(recipe_parity.safe_file(root, path, target=True), path)
            self.assertFalse(recipe_parity.safe_file(root, 'src/link.json'))

    def test_valid_paths_only_check_presence_not_behavior(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / 'src').mkdir()
            (root / 'src/recipe.json').write_text('{}')
            (root / 'evidence.txt').write_text('synthetic example; not a game test')
            # A tiny fixture verifies structural rules, not actual recipe behavior.
            expected = self.expected[:1]
            entry = {**expected[0], 'status': 'verified', 'targets': ['src/recipe.json'],
                     'evidence': ['evidence.txt']}
            manifest = {'schema_version': 1, 'baseline_commit': recipe_parity.BASELINE,
                        'entries': [entry], 'coverage': [
                            {'id': name, 'status': 'verified', 'evidence': ['evidence.txt']}
                            for name in recipe_parity.COVERAGE]}
            self.assertEqual(recipe_parity.validate(manifest, expected, root), ([], 0))


if __name__ == '__main__':
    unittest.main()

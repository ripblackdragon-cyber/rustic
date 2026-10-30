import json
import sys
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / 'tools'))
import recipe_audit
import registry_audit
import verify_upstream


class RecipeExtractionTests(unittest.TestCase):
    def test_ignores_strings_comments_and_balances_nested_calls(self):
        text = '''// GameRegistry.addSmelting(fake())
String s = "new BrewingBarrelRecipe(fake())";
/* new CrushingTubRecipe(fake()) */
GameRegistry.addShapedRecipe(id("a)"), "S S", '(', value(nested(1)));
new BrewingBarrelRecipe(output(1), input("\\\" )"));
'''
        records = recipe_audit.extract(text)
        self.assertEqual([r[0] for r in records], [4, 5])
        self.assertEqual(records[0][2], 'GameRegistry.addShapedRecipe(id("a)"), "S S", \'(\', value(nested(1)))')
        self.assertTrue(records[1][2].endswith('input("\\\" )"))'))

    def test_unbalanced_expression_is_an_error(self):
        with self.assertRaises(ValueError):
            recipe_audit.extract('new BrewingBarrelRecipe(foo(')

    def test_expected_original_expressions_are_indexed_verbatim(self):
        source = recipe_audit.SOURCE.read_text(encoding='utf-8')
        records = recipe_audit.extract(source)
        self.assertEqual(len(records), 123)
        report = recipe_audit.OUTPUT.read_text(encoding='utf-8')
        for line, kind, expression in records:
            with self.subTest(line=line):
                self.assertIn(f'### {kind} — línea {line}', report)
                self.assertIn('```java\n' + expression + '\n```', report)
        self.assertIn('"S  ", "SS ", "SSS"', report)
        self.assertIn('ModFluids.AMBROSIA', report)


class RegistryAuditTests(unittest.TestCase):
    def test_report_regenerates_identically(self):
        expected = registry_audit.OUTPUT.read_text(encoding='utf-8')
        with tempfile.TemporaryDirectory() as directory:
            output = Path(directory) / 'report.md'
            with patch.object(registry_audit, 'OUTPUT', output):
                registry_audit.main()
            self.assertEqual(output.read_text(encoding='utf-8'), expected)
        self.assertIn('## Entidades (2)', expected)
        self.assertIn('## Entradas del almanaque (35)', expected)
        self.assertIn('`rustic:undying`', expected)


class UpstreamVerificationTests(unittest.TestCase):
    def test_pinned_snapshots_match_upstream_blobs(self):
        for name in verify_upstream.SNAPSHOTS:
            with self.subTest(snapshot=name):
                self.assertEqual(verify_upstream.verify(name), ([], [], []))

    def test_detects_missing_extra_and_changed_files(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            (root / 'provenance').mkdir()
            (root / 'snapshot').mkdir()
            (root / 'snapshot/changed').write_text('modified')
            (root / 'snapshot/extra').write_text('extra')
            (root / 'provenance/example.json').write_text(json.dumps({
                'blobs': {'changed': 'wrong', 'missing': 'also wrong'}
            }))
            with patch.object(verify_upstream, 'ROOT', root), patch.dict(verify_upstream.SNAPSHOTS, {'example': 'snapshot'}):
                self.assertEqual(verify_upstream.verify('example'), (['missing'], ['extra'], ['changed']))


if __name__ == '__main__':
    unittest.main()

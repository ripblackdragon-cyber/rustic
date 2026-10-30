import hashlib
import json
import re
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'original/rustic-pre-1.2.0/src/main/resources'


class InventoryTests(unittest.TestCase):
    def test_resource_manifest_matches_files(self):
        report = (ROOT / 'RUSTIC_ASSETS.md').read_text(encoding='utf-8')
        rows = re.findall(r'^\| \[.*?\]\((original/[^)]+)\) \| `([0-9a-f]{64})` \|', report, re.M)
        indexed = {path: digest for path, digest in rows}
        self.assertEqual(len(rows), len(indexed), 'duplicate manifest paths')
        actual = {p.relative_to(ROOT).as_posix(): hashlib.sha256(p.read_bytes()).hexdigest()
                  for p in RES.rglob('*') if p.is_file()}
        self.assertEqual(indexed, actual)

    def test_recipe_json_inventory(self):
        report = (ROOT / 'RUSTIC_RECIPES.md').read_text(encoding='utf-8')
        recipe_dir = RES / 'assets/rustic/recipes'
        for path in recipe_dir.glob('*.json'):
            with self.subTest(path=path.name):
                json.loads(path.read_text(encoding='utf-8'))
                self.assertIn(f']({path.relative_to(ROOT).as_posix()})', report)

    def test_java_source_inventory(self):
        report = (ROOT / 'RUSTIC_FEATURES.md').read_text(encoding='utf-8')
        source = ROOT / 'original/rustic-pre-1.2.0/src/main/java/rustic'
        for path in source.rglob('*.java'):
            with self.subTest(path=path.name):
                self.assertIn(f']({path.relative_to(ROOT).as_posix()})', report)


if __name__ == '__main__':
    unittest.main()

import json
from pathlib import Path
import tempfile
import unittest
import zipfile

from tools.check_development_jar import check_jar


class JarPackagingTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        resources = self.root / 'src/main/resources'
        resources.mkdir(parents=True)
        (resources / 'example.txt').write_bytes(b'example')
        (self.root / 'licenses').mkdir()
        (self.root / 'THIRD_PARTY_NOTICES.md').write_bytes(b'notice')
        (self.root / 'licenses/GRADLE-APACHE-2.0.txt').write_bytes(b'license')
        self.jar = self.root / 'dev.jar'

    def write_jar(self, resource=b'example'):
        metadata = {'id': 'rustic', 'version': '0.0.0-dev',
                    'entrypoints': {'main': ['rustic.fabric.RusticFabric']},
                    'depends': {'minecraft': '=26.3'}}
        with zipfile.ZipFile(self.jar, 'w') as jar:
            jar.writestr('fabric.mod.json', json.dumps(metadata))
            for name in ('RusticFabric', 'RusticItems', 'RusticFoods', 'RusticCreativeTabs'):
                jar.writestr(f'rustic/fabric/{name}.class', b'\xca\xfe\xba\xbe')
            jar.writestr('example.txt', resource)
            jar.writestr('META-INF/THIRD_PARTY_NOTICES.md', b'notice')
            jar.writestr('META-INF/licenses/GRADLE-APACHE-2.0.txt', b'license')

    def test_matching_jar(self):
        self.write_jar()
        count, digest = check_jar(self.jar, self.root)
        self.assertEqual(count, 1)
        self.assertEqual(len(digest), 64)

    def test_changed_resource_is_rejected(self):
        self.write_jar(b'changed')
        with self.assertRaisesRegex(ValueError, 'resource differs'):
            check_jar(self.jar, self.root)


if __name__ == '__main__':
    unittest.main()

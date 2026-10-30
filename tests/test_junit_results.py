import tempfile
import unittest
from pathlib import Path
import xml.etree.ElementTree as ET

from tools.check_junit_results import EXPECTED, check_results


class JUnitResultCheckTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.directory = Path(self.temp.name)
        self.root = ET.Element('testsuite')
        for name in EXPECTED:
            ET.SubElement(self.root, 'testcase', classname='rustic.fabric.RusticFoodsTest', name=f'{name}()')

    def write(self):
        ET.ElementTree(self.root).write(self.directory / 'TEST-rustic.fabric.RusticFoodsTest.xml')

    def test_parenthesized_cases_pass(self):
        self.write()
        check_results(self.directory)

    def test_missing_and_duplicate_cases_fail(self):
        missing = self.root[0]
        self.root.remove(missing)
        self.write()
        with self.assertRaisesRegex(ValueError, 'differ'):
            check_results(self.directory)
        self.root.append(missing)
        self.root.append(ET.fromstring(ET.tostring(missing)))
        self.write()
        with self.assertRaisesRegex(ValueError, 'Duplicate'):
            check_results(self.directory)

    def test_skipped_and_failed_cases_fail(self):
        for tag in ('skipped', 'failure', 'error'):
            with self.subTest(tag=tag):
                child = ET.SubElement(self.root[0], tag)
                self.write()
                with self.assertRaisesRegex(ValueError, 'did not pass'):
                    check_results(self.directory)
                self.root[0].remove(child)


if __name__ == '__main__':
    unittest.main()

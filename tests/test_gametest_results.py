import tempfile
import unittest
from pathlib import Path
import xml.etree.ElementTree as ET

from tools.check_gametest_results import EXPECTED, check_results


class MinecraftGameTestResultTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.path = Path(self.temp.name) / 'report.xml'
        self.root = ET.Element('testsuite')
        suite = ET.SubElement(self.root, 'testsuite')
        self.cases = [ET.SubElement(suite, 'testcase', name=name) for name in sorted(EXPECTED)]
        ET.SubElement(suite, 'testcase', name='minecraft:always_pass')

    def write(self):
        ET.ElementTree(self.root).write(self.path)

    def test_expected_cases_pass(self):
        self.write()
        self.assertEqual(check_results(self.path), len(EXPECTED))

    def test_missing_duplicate_and_unknown_cases_fail(self):
        suite = self.root[0]
        case = self.cases[0]
        suite.remove(case)
        self.write()
        with self.assertRaisesRegex(ValueError, 'differ'):
            check_results(self.path)
        suite.append(case)
        duplicate = ET.fromstring(ET.tostring(case))
        suite.append(duplicate)
        self.write()
        with self.assertRaisesRegex(ValueError, 'Duplicate'):
            check_results(self.path)
        suite.remove(duplicate)
        ET.SubElement(suite, 'testcase', name='rustic_gametest:unexpected')
        self.write()
        with self.assertRaisesRegex(ValueError, 'differ'):
            check_results(self.path)

    def test_failure_error_skip_are_rejected(self):
        for tag in ('failure', 'error', 'skipped'):
            with self.subTest(tag=tag):
                child = ET.SubElement(self.cases[0], tag)
                self.write()
                with self.assertRaisesRegex(ValueError, 'did not pass'):
                    check_results(self.path)
                self.cases[0].remove(child)

    def test_missing_report_fails(self):
        with self.assertRaises(FileNotFoundError):
            check_results(self.path)


if __name__ == '__main__':
    unittest.main()

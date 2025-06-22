$xml = @"
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="SingleTestSuite">
  <test name="SingleTest">
    <classes>
      <class name="MavenProject.testingandlearning.DynamicUIComponentsTest"/>
    </classes>
  </test>
</suite>
"@

Set-Content -Path dynamic-suite.xml -Value $xml

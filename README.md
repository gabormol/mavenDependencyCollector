# mavenDependencyCollector
This little tool helps you to see all the top level dependencies, your Maven project has. It searches your POM files recursively and removes the duplicates + test dependencies (right now). 

The input is the path to your project, the output will be a basic excel file without any formatting. 

<h3>Usage:</h3>
<p> 1) Create an executable jar file</p>
<p> 2) java -jar << your_jar_file_name >>.jar << path_to_your_project >> </p>

Have fun, any comments are welcome!


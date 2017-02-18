# mavenDependencyCollector
This little tool helps you to see all the top level dependencies, your Maven project has. It searches your POM files recursively and removes the duplicates + test dependencies (right now). 

The input is the path to your project, the output will be a basic excel file without any formatting. 

Usage:
1) Create an executable jar file
2) java -jar <your_jar_file_name>.jar <path_to_your_project>

Have fun, any comments are welcome!


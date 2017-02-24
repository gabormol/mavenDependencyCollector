# mavenDependencyCollector
This little tool helps you to see all the top level dependencies, your Maven project has. It searches your POM files recursively and removes the duplicates + test dependencies (right now). It can handle the dependencyManagement feature of the Maven as well and in addition you can remove artifacts from the output, containing a certain string or matching a Java regex (not tested but should work).

The input is the path to your project, the output will be a basic excel file without any formatting. 

<h3>Usage:</h3>
<ol>
<li> Create an executable jar file</li>
<li> <code>java -jar &lttyour_jar_file_name_.jar&gt &lttpath_to_your_project&gt &ltstringpattern_for_match_removals&gt &ltproject_artifactId-s_of_dependencies_not_needed_to display&gt</code> </li>
</ol>

<h4>Example 1:</h4>

<code>java -jar checkProductionDependencies.jar c:\My_project_sources\ </code>

<h4>Example 2:</h4>

<code>java -jar checkProductionDependencies.jar c:\My_project_sources\ myProjectSpecificThings </code>

<h4>Example 3:</h4>

<code>java -jar checkProductionDependencies.jar c:\My_project_sources\ myProjectSpecificThings artifact-id1,artifact-id2</code>

Have fun, any comments are welcome!


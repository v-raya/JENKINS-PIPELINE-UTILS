# lint

## Usage

```groovy
  lint()
```


## Examples

```groovy
   stage('Linting Stage') {
      lint()
   }
```

## Conventions
Linting will run on 3 different types of projects.

* **Ruby** 
<p>	
The existence of **.rubocop.yml** in the root directoy of the project will initiate a rubocop run.  
The command run is ```rubocop```
</p>
* **Javascript** 
<p>
The existence of **.eslintrc** in the root directoy **OR** the existence of **eslintConfig** in package.json will initiate a run of eslint.  
The command run is ```npm run lint```
</p>
* **Java** 
<p>
The existence of **build.gradle** in the root directoy will initiate a run of sonarqube.  
The command run is </p>

```
	withSonarQubeEnv('Core-SonarQube') {
		buildInfo = rtGradle.run buildFile: 'build.gradle', switches: '--info', tasks: 'sonarqube'
    }  
```


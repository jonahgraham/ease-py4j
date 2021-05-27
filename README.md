# ARCHIVED

All changes have been contributed back to their upstream repositories, so this repo is archived. See https://github.com/jonahgraham/EASE-Python-Examples for the tutorial.


# ease-py4j
EASE Python support using py4j

# Developing
Use Eclipse Committers or similar as a starting platform.

- **Step 1**: Clone and import all projects in the following repos (from the py4j-ease branch):

  - https://github.com/jonahkichwacoders/ease-py4j/ -- this project
  - https://github.com/jonahkichwacoders/py4j/
  - https://github.com/jonahkichwacoders/org.eclipse.ease.core/
  - https://github.com/jonahkichwacoders/org.eclipse.ease.scripts/
  - https://github.com/jonahkichwacoders/org.eclipse.ease.modules/

- **Step 2**: Set target platform to [Developers.target](https://github.com/jonahkichwacoders/org.eclipse.ease.core/blob/py4j-ease/releng/org.eclipse.ease.releng.target/Developers.target) (in org.eclipse.ease.core/releng project org.eclipse.ease.releng.target)

- **Step 3**: Some minor further setup needs to be done:

  - Ignore errors of missing API Baselines (go to Preferences -> Plug-in Development -> API Baselines and choose Missing API baseline = Ignore)
  - Close org.eclipse.ease.sample.feature and org.eclipse.ease.sample.project projects
    - These projects are templates in the event new projects need to be made
  - Follow How to fix this project.txt in org.eclipse.ease.helpgenerator
    - alternatively close the project

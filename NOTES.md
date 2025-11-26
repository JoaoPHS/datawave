# Migration to Gradle 8.5 and 7.6.4 has been tested

 Gradle 8.5 worked, but it would have been necessary to compile each module separately, 
 which for a project of this size would be unfeasible to have to do this, 
 and it is not scalable for more recent versions of Gradle. 
 Precisely due to serious problems with the project's circular dependencies.

I could "improvise", but I have to think about the long term and stability... I could try older versions of gradle, but they would behave differently and the project would remain in a stationary situation.

Why did I try to migrate to gradle?

### Gradle:

- It is significantly faster in incremental builds
- Reduces configuration files and facilitates maintenance
- Offers more control and clarity when resolving dependencies
- It is more flexible and efficient in performing tasks
- Groovy code, more readable and flexible
- Provides clearer, more useful feedback
- Wrapper included
- Better integration with JUnit 5
- Build smart cache

Even though I have experience with many failed builds (mainly in C++ and react native lol) 
and it was very stressful, frustrating this failed migration to gradle, I learned a lot and in the end that's what matters. 
Failures serve as learning.

## Research into areas of improvement and testing is still ongoing...

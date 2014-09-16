atom
====

Advanced Transparent Object Manager: Hibernate backend, GWT frontend, dynamically generated with the help of reflection

The motivation of this project is to allow easy data management of data with quickly evolving data structures.

This is achieved by eliminating the need to manually propagating changing metadata through all tiers.
The developer only needs to change the structure in one place, the "domain object" classes.
Hibernate will take care of adapting the database model, and the GUI is designed to adapt to changing data structures by the use of reflection (enabled by the project gwt-ent https://code.google.com/p/gwt-ent/) 
# jpf-progress

This repo is a series of listener extensions for checking progress while jpf is searching. Currently, there is only one listener that's implemented by the paper `JPFBAR`. More listeners are expected to be added. 

### Install 

- Clone this repo under jpf home.  The ideal structure is:
  - jpf-core
  - jpf-progress
  - ...
- Inside `jpf-progress`, do `ant build`



### Usage

- Inside the `.jpf` file you are trying to run, add 

```properties
@using = jpf-progress
listener = gov.nasa.jpf.listener.PathCountEstimator
...

```



### Reference

- `PathCountEstimator.java` credit to: [K. Wang, H. Converse, M. Gligoric, S. Misailovic, and S. Khurshid. A progress bar for the JPF search using program executions. In JPF, 2018.](https://dl.acm.org/citation.cfm?id=3302419)

  


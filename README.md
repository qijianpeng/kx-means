# k*-means
[中文版](./README_ch.md)

An effective and efficient hierarchical K-means clustering algorithm. [[Download pdf]](https://doi.org/10.1177/1550147717728627)

## Usage
- Main Class`ctl/MainTime.java`

Runtime parameters`etc/Configuration.java`:
```java
  public static int k = 20; // number of clusters at last. 
  public static String path ="data/data20groups_500d.txt";//dataset path
  public static final int dmStart =0;//1st attribute position
  public static final int dm =499;//dimensions of datasets
  public static final int classPos =499;//position of label
  public static String separated = ","; //separator
```

## Requirements
- jdk 7+

## Citing
If you find _k_*-means useful in your research, we ask that you cite the following paper:

- Journal Version:
```
@article{kxmeansJournal,
  author    = {Jianpeng Qi and
               Yanwei Yu and
               Lihong Wang and
               Jinglei Liu and
               Yingjie Wang},
  title     = {An effective and efficient hierarchical \emph{K}-means clustering
               algorithm},
  journal   = {International Journal of Distributed Sensor Networks},
  volume    = {13},
  number    = {8},
  year      = {2017},
  url       = {https://doi.org/10.1177/1550147717728627},
  doi       = {10.1177/1550147717728627}
}
```

- Conference version:
```
@inproceedings{kxmeansConference,
  title={K*-Means: An Effective and Efficient K-Means Clustering Algorithm},
  author={Qi, Jianpeng and Yu, Yanwei and Wang, Lihong and Liu, Jinglei},
  booktitle={IEEE International Conferences on Big Data and Cloud Computing},
  pages={242-249},
  year={2016},
}
```
## Misc
- Free software: GPLv3 license
- Contact: yuyanwei@ytu.edu.cn

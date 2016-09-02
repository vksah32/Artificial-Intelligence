# Motion Planner

## Overview 
In this project, we will implement motion planners for two systems: a planar robot arm (with state given by the angles of the joints), and a kinematic car (with state given by the x,y location of a point on the robot, and the angle that the robot makes with the horizontal).


### Probabilistic Roadmap (PRM)
For the arm robot, you write a Probabilistic Roadmap planner. 

There are two phases in the planner:

Roadmap generation phase: generate a roadmap to represent the configuration space.

Query phase: for a given query, find a path connecting start and goal.

#### Roadmap generation phase

The goal of this phase is to build a roadmap to represent the configuration space. Initially, the roadmap only contains start and goal configurations. Then, PRM iteratively generates a random configuration, and adds this random configuration with edges connecting some neighboring configurations to the roadmap if the random configuration and the connection are collision-free.

There are variants of PRM depending on connecting strategies:

* always connect to k nearest neighbors, where k is a fixed constant.

* connect to all neighbors with distance smaller than d for a fixed constant d.

To implement PRM, we'll need a sampling method to generate a configuration in the configuration space, a collision detection method to check if one configuration is collision-free, a steering method that can attempt to connect configurations, and a k-nearest-neighbors query.

#### Query phase

The goal in the query phase is to find a trajectory connecting two given configurations. Given two given configurations and a roadmap, we can use A-star search to find the shortest path connecting two configurations on the roadmap.

### Rapidly Exploring Random Tree

For the second part, our task is to construct a Rapidly-exploring Random Tree RRT to plan motions for a car-like robot.

The idea is to represent the configuration by a tree. Initially, the tree only contains start. In each iteration, RRT generates a random configuration r and then find r's nearest neighbor, near, on the tree. From the nearest neighbor, the algorithm applies a random control with duration delta to reach another configuration, new. If the motion from near to new is collision-free, then new is added to the tree.

The tree will gradually cover the configuration space and get closer to the goal. RRT returns the trajectory determined by the unique path from the goal's nearest neighbor to the start on the tree.

The above is just the original RRT. There are some variants of RRT based on the following parameters:

        goal bias: with a small probability like 1/100, set r to be the goal to enforce the tree growing toward the goal.
        
        k-RRT: instead of growing just one near, grow k nearest neighbors for some constant k (or grow all neighbors with distance smaller than d for some constant d).
        
        random delta: set delta value randomly.
        
        good control: instead of picking a random control, try all possible controls and pick the one that generates a new configuration closest to r.


Each car has a set of possible controls and there is a method (Robot.move) to simulate any motion, which is described by a pair of control and duration.

There are four types of cars in this assignment: 

        Dubins car (Links to an external site.): a car that can only drive forward.
        Reeds-Shepp car (Links to an external site.): a car that can drive forward and backward.
        Differential drive (Links to an external site.): a car that can drive forward/backward and spin.
        Omnidirectional drive: a car that can move in any direction.

Refer to report_vivek.pdf for implementation details.

(adapted from Prof. Balkcom's notes)

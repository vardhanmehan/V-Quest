# V-Quest
For my Advanced Data Structures class (CMSC420), we created a soft implementation of Map Quest. 

The main file, MeeshQuest.java, is used to parse an XML file with commands to adjust our map of cities. The processing of commands happens in Command.java. In this file, we maintain a data dictionary for cities by name, a TreeSet sorted by name, and cities by location, a TreeMap sorted by location. This allows us to perform operations on the tree based on specific criterion. 

Our spatial map is represented by a Point Region QuadTree (PR QuadTree), which is a key-space partitioned search trie in which all keys are located in the leaves of the tree. This is a good implementation for creating the spatial map as it ensures that lookup time is, at worst, O(log n). Furthermore, this allows us to put a limit on the number of keys we can have in our spatial map, as defined by the spatial height and spatial width variables.

The PR QuadTree implementation is defined with three types of nodes, each of which extend the Node abstract class:
- A grey node is a "guide node" that contains information about quadrants and the children. Due to the fact that this is a trie, we can see that the grey node will not contain ANY final key, value pairs, only references to at most 4 child nodes. 
- A black node, also known as a leaf node, contains information about the city it contains. 
- Finally, a white node is a singleton class that represents an empty child pointer. This saves a lot of space as each empty child pointer in a grey node will point to the same instance!

Some interesting functions to look at are the citiesInRange and processNearestCity functions. The citiesInRange function takes an x coordinate, a y coordinate, and a radius, and returns all cities within that range. The processNearestCity function takes x and y coordinate parameters and returns the nearest city. To do this, I created a priority queue based on a custom NodeDistance Comparator and iterated through the priority queue until a black node, the nearest node, was found. 

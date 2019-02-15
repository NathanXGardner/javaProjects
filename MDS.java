/*
    Author: Nathan Gardner
    Class:  CS 3345.004 Data Structures and Introduction to Algorithmic Analysis 
    Project 3
    This program contains 2 Classes - an MDS class (that has all the methods for maintaining a system of data)
    and an Item class - it consists of an id, price, List of description items and a hashSet of description items
    A hashMap of IDs mapped to Items is used for quick access of data and checking to see if contained
    A treeMap of IDs mapped to Items is used for looping thorugh the data 
    A hashSet of descriptions integers is used for the Item description list for quick access of contents in description
    The methods used are:
    find(id) - given id return corresponding item price or 0 OW
    delete(id) -  remove the id from the system and return sum of description contents from item, or 0 if no id exists
    insert(id,price,list) - given info for an item, update price, and either update or retain previous description
                            1 is returned for updated and 0 is returned for retained
    findMaxPrice(n) - given an int n look through each item's corresponding description and return the smallest price of 
                      those that contain n, or 0 OW
    findMinPrice(n) - given an int n look through each item's corresponding description and return the largest price of 
                      those that contain n, or 0 OW
    findPriceRange(n,l,h) - given a range [l,h] and an int n look through each item's corresponding description and if n
                            is contained in the description and the item's price falls in the range, then add that to a 
                            counter that includes every item that meets these two criteria, and return 0 OW
    removeNames(id,list) - given an id and a list, look at the id's corresponding item and determine if the list contents
                           are contained in the item's description, if any are add up all of those that matched and then
                           remove from the description. 0 is returned if the id did not exist, list was empty or if no 
                           list contents matched
*/

package nxg150630;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

public class MDS {
    
    public HashMap<Integer, Item> hashMap;   
    public TreeMap<Integer, Item> treeMap; 
    
    public class Item {
        private int id;
        private int price;
        private List<Integer> description;
        private HashSet<Integer> descSet;
        
        Item() {
            id = 0;
            price = 0;
        }
        
        Item(int i, int p, List<Integer> d) {
            id = i;
            price = p;
            description = new ArrayList<>();
            descSet = new HashSet<>();
            
            for(Integer x : d) {
                if(!descSet.contains(x)) {
                    descSet.add(x);//also put it into a HashSet for quick lookup
                    description.add(x);//put all of the description into the list
                }
            }
        }              
        
        public void setItem(int i, int p) {
            id = i;
            price = p;         
        }        
        
        public void printDesc() {
            System.out.println("The description list is:");
            for(Integer x : description)
                System.out.print(x + " ");
            System.out.println("\nThe description set is:");
            for(Integer x : description) {
                if(descSet.contains(x))
                    System.out.print(x + " ");
            }
            System.out.println();
        }       
        
        public int compareTo(Item item) {
            if(id < item.id) 
                return -1;
             else if(id > item.id) 
                return 1;
             else 
                return 0;            
        }
        
        @Override
        public int hashCode() {
            return id;
        }
        
        @Override
        public boolean equals(Object another) {
            if(another == null) return false;//no reference means no connection
            if(this == another) return true;// the same objects must have the same address
            Item other = (Item) another;//now we can cast to assume they are the same type
            boolean result = (id == other.id);//and in this case we compare their ids for equality
            return result;
        }
    }
    
    /* Constructors: */
    public MDS() {
        hashMap = new HashMap<Integer, Item>();//maps an ID to an Item | for extracting specific Items
        treeMap = new TreeMap<Integer, Item>();//maps an ID to an Item | for iteraton over the system data
    }
    
    /* Public methods of MDS. Do not change their signatures.
       __________________________________________________________________
       a. Insert(id,price,list): insert a new item whose description is given
       in the list.  If an entry with the same id already exists, then its
       description and price are replaced by the new values, unless list
       is null or empty, in which case, just the price is updated. 
       Returns 1 if the item is new, and 0 otherwise.
       
       Efficiency: O(description.size + log[treeMap.size]) - whichever term is bigger will dominate
       Extracting the item given an id located in a hashMap takes constant time
       [In the constructor] Looping through the description list adding each integer takes description.size time
       putting the id into the hashMap takes constant time
       putting the id into the treeMap takes O(logn) where n is the treeMap.size
    
       MODIFICATION METHOD: all data structures must be updated   
    */
    public int insert(int id, int price, java.util.List<Integer> list) {
        Item x = hashMap.get(id);//get() automatically calls contains() which will call the HashMap find()
        Item y = new Item();
        if(list.isEmpty()) {//we know we want to retain the description, AND make it non-null, yet empty if not made yet
            if(x == null) {//the item is NOT in the list, because the ID mapped to null
                y.description = new ArrayList<>();
                y.descSet = new HashSet<>();                
            } else {
                y.descSet = x.descSet;
                y.description = x.description;
            }
            y.setItem(id,price);//update price including id
        
        } else {//list has 1 or more elements
            y = new Item(id,price,list);//update price including id
        }

        //regardless of description updates:
        hashMap.put(id, y);
        treeMap.put(id, y);        
        return x == null ? 1 : 0;
    }
    
    /*
       b. Find(id): return price of item with given id (or 0, if not found).      
       
       Efficiency: O(1) - extracting the price from the id located in a hashMap takes constant time
       
       ACCESS METHOD: use most efficient search methods with best data structure | NO modifications should be made
    */
    public int find(int id) {
        Item x = hashMap.get(id);//get() automatically calls contains() which will call the HashMap find()
        return x == null ? 0 : x.price;
    }
    
    /* 
       c. Delete(id): delete item from storage.  Returns the sum of the
       ints that are in the description of the item deleted,
       or 0, if such an id did not exist.  
    
       Efficiency: O(description.size + log[treeMap.size]) - Each description integer must removed and added to the sum
       removing from treeMap takes O(logn) where n is the number of Items in the TreeMap
       removing from hashMap takes O(1)
       extracting the price from the item, comparing the price and then updating the max also take constant time      
    
       MODIFICATION METHOD: all data structures must be updated
    */
    public int delete(int id) {
        Item x = hashMap.get(id);//get() automatically calls contains() which will call the HashMap find()
        if(x == null) //ID is not in the system
            return 0;
        int sum = 0;
        for(Integer i : x.description) 
            sum += i;
        
        hashMap.remove(id);
        treeMap.remove(id); 
        return sum;
    }
    
    /* 
       d. FindMinPrice(n): given an integer, find items whose description
       contains that number (exact match with one of the ints in the
       item's description), and return lowest price of those items.
       Return 0 if there is no such item.
 
       Efficiency: O(Items in System) - Each item must be checked to see if description contains n
       to see if the description HashSet contains n takes constant time
       extracting the price from the item, comparing the price and then updating the min also take constant time      
    
       ACCESS METHOD: use most efficient search methods with best data structure | NO modifications should be made
    */
    public int findMinPrice(int n) {
        if(hashMap.isEmpty())
            return 0;
        int minPrice = Integer.MAX_VALUE;
        for(Integer ID : treeMap.keySet()) {
            if(hashMap.get(ID).descSet.contains(n)) {
                int price = hashMap.get(ID).price;
                if(price < minPrice)//update minimum
                    minPrice = price;                 
            }  
        }
        return minPrice < Integer.MAX_VALUE ? minPrice : 0;
    }
    
    /* 
       e. FindMaxPrice(n): given an integer, find items whose description
       contains that number, and return highest price of those items.
       Return 0 if there is no such item.
    
       Efficiency: O(Items in System) - Each item must be checked to see if description contains n
       to see if the description HashSet contains n takes constant time
       extracting the price from the item, comparing the price and then updating the max also take constant time    
       
       ACCESS METHOD: use most efficient search methods with best data structure | NO modifications should be made
    */
    public int findMaxPrice(int n) {
        if(hashMap.isEmpty())
            return 0;
        int maxPrice = 0;
        for(Integer ID : treeMap.keySet()) {
            if(hashMap.get(ID).descSet.contains(n)) {
                int price = hashMap.get(ID).price;
                if(price > maxPrice)//update minimum
                    maxPrice = price;                 
            }  
        }
        return maxPrice;
    }
    
    /* 
       f. FindPriceRange(n,low,high): given int n, find the number
       of items whose description contains n, and in addition,
       their prices fall within the given range, [low, high].
       
       Efficiency: O(Items in System) - Each item must be checked to see if description contains n
       to see if the description HashSet contains n takes constant time
       extracting the price from the item, comparing the price and then updating the counter also take constant time
    
       ACCESS METHOD: use most efficient search methods with best data structure | NO modifications should be made
    */
    public int findPriceRange(int n, int low, int high) {
        int count = 0;
        for(Integer ID : treeMap.keySet()){//every item will be checked 
            if(hashMap.get(ID).descSet.contains(n)) {//constant time lookup
                int price = hashMap.get(ID).price;
                if(price >= low && price <= high)//only prices within range
                    count++;
            }
        }
        return count;
    }
    
    /*
      g. RemoveNames(id, list): Remove elements of list from the description of id.
      It is possible that some of the items in the list are not in the
      id's description.  Return the sum of the numbers that are actually
      deleted from the description of id.  Return 0 if there is no such id.
    
      Efficiency: O(list.size*description.size) - Each potential integer to be removed must be checked to see if description contains it
      to see if the description HashSet contains n takes constant time
      removing the description from the list takes an amount of time equal to the number shifts that need to be made
      getting the item from the hashMap, cheking if it is null removing from the descSet and updating sum take constant time
    
      INTERNAL MODIFICATION METHOD: because data is being manipulated within a given id, no updates are needed    
    */
    public int removeNames(int id, java.util.List<Integer> list) {
        Item item = hashMap.get(id);//get() automatically calls contains() which will call the HashMap find()
        if(item == null || item.description == null || item.description.isEmpty()) 
            return 0;
        int sum = 0;
        
        for(Integer x : list) {//loop thru removal list for values and then use the hashSet to see if contained in the description
            if(item.descSet.contains(x)) {//should take constant time to see if the removal item is in the description
                sum += x;
                item.descSet.remove(x);//constant time to remove from set
                item.description.remove(x);
            }   
        }
        return sum;
    }
}
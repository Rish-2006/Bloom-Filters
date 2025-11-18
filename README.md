BloomFilterVisualizer/
├── README.md
└── Java/
    ├── BloomFilter.java
    └── BloomFilterGUI.java

# 🔎 Bloom Filter Visualizer

A Java Swing application designed to visualize the internal workings of a **Bloom Filter** data structure. This tool allows users to interactively insert elements, check for their potential presence, and observe the resulting changes in the bit array (represented as a grid).

## ✨ Features

* **Interactive Visualization:** A $9 \times 9$ grid displays the Bloom Filter's bit array (up to 81 bits). Set bits are highlighted in **green** when an element is inserted.
* **Insert Elements:** Add a string element and immediately see which bits are set by its $k=7$ hash functions.
* **Lookup Elements:** Check if a string is **"Possibly present"** or **"Definitely not present"** and see the seven indices checked.
* **Statistics:** View the current number of inserted elements and the estimated **False Positive Rate (FPR)**.
* **History Log:** A scrollable list logs all inserted elements along with the indices calculated by their hash functions.

---

## ⚙️ Implementation Details

The core functionality is split into two Java files, both using the `Java` package:

### `BloomFilter.java`
This class implements the generic Bloom Filter data structure (`BloomFilter<T>`). It calculates the optimal size ($m$) based on expected elements and false positive probability.

### `BloomFilterGUI.java`
This class handles the graphical user interface using **Java Swing**. It uses **Java Reflection** to access and visualize the private fields (like the bit array and hash functions) of the `BloomFilter` class.

---

## 🚀 Getting Started

### Requirements
* Java Development Kit (JDK) 8 or higher.

### Compile and Run

Navigate to your project's root directory (`BloomFilterVisualizer/`) in your terminal.

**Compile the files:**

```bash
javac Java/BloomFilter.java Java/BloomFilterGUI.java

java Java.BloomFilterGUI

---

### 3. `BloomFilter.java` (Copy and Paste into the `Java` folder)

```java
package Java;
import java.util.BitSet;
import java.util.function.Function;
public class BloomFilter<T>{
    private BitSet bitArray;
    private int size;
    private Function<T,Integer>[] hashSet1=new Function[]{
        s -> s.hashCode()*3+7,
        s -> s.hashCode()*5+11,
        s -> s.hashCode()*7+13,
        s -> s.hashCode()*11+17,
        s -> s.hashCode()*13+19,
        s -> s.hashCode()*17+23,
        s -> s.hashCode()*19+29
    };
    private Function<T,Integer>[] hashSet2=new Function[]{
        s -> s.hashCode()*2+3,
        s -> s.hashCode()*3+5,
        s -> s.hashCode()*5+7,
        s -> s.hashCode()*7+11,
        s -> s.hashCode()*11+13,
        s -> s.hashCode()*13+17,
        s -> s.hashCode()*17+19
    };
    public BloomFilter(int expectedNumOfElements,double requiredFalsePositiveProbability){
        this.size=Math.max(1,(int)(-expectedNumOfElements*Math.log(requiredFalsePositiveProbability)/(Math.pow(Math.log(2),2))));
        this.bitArray=new BitSet(size);
    }
    public int size(){
        return size;
    }
    public double currentFalsePositiveRate(int currentNumOfElements){
        double P=Math.pow((1-Math.exp(-((7.0*currentNumOfElements)/(double)size))),7.0);
        return P*100;
    }
    public void insert(T item){
        for(int i=0;i<7;i++){
            int h1=hashSet1[i].apply(item);
            int h2=hashSet2[i].apply(item);
            int index=Math.abs((h1+h2)%size);
            bitArray.set(index);
        }
    }
    public boolean lookup(T item){
        for(int i=0;i<7;i++){
            int h1=hashSet1[i].apply(item);
            int h2=hashSet2[i].apply(item);
            int index=Math.abs((h1+h2)%size);
            if(!bitArray.get(index)){
                return false;
            }
        }
        return true;
    }
}
4. BloomFilterGUI.java (Copy and Paste into the Java folder)
Java

package Java;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.BitSet;
public class BloomFilterGUI extends JFrame{
    private static final int GRID_SIZE=9;
    private static final int NUM_CELLS=GRID_SIZE*GRID_SIZE;
    private JPanel[][] cells=new JPanel[GRID_SIZE][GRID_SIZE];
    private BloomFilter<String> bloomFilter;
    private int numElements=0;
    private DefaultListModel<String> elementListModel;
    private JList<String> elementList;
    public BloomFilterGUI(){
        bloomFilter=new BloomFilter<>(56,0.5);
        setTitle("Bloom Filter Visualizer");
        setSize(550,650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8,8));
        getContentPane().setBackground(new Color(25,25,25));
        elementListModel=new DefaultListModel<>();
        elementList=new JList<>(elementListModel);
        elementList.setBackground(Color.BLACK);
        elementList.setForeground(Color.WHITE);
        elementList.setFont(new Font("Consolas",Font.PLAIN,12));
        JScrollPane scrollPane=new JScrollPane(elementList);
        scrollPane.getViewport().setBackground(Color.BLACK);
        scrollPane.setBackground(Color.BLACK);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.GRAY,1),
                "Inserted Elements and Hash Indexes",
                0,0,new Font("Arial",Font.BOLD,13),Color.WHITE));
        scrollPane.setPreferredSize(new Dimension(530,130));
        JPanel gridPanel=new JPanel(new GridLayout(GRID_SIZE,GRID_SIZE,1,1));
        gridPanel.setBackground(Color.DARK_GRAY);
        gridPanel.setPreferredSize(new Dimension(400,400));
        for (int i=0;i<GRID_SIZE;i++){
            for (int j=0;j<GRID_SIZE;j++){
                int index=i*GRID_SIZE+j;
                JPanel cell=new JPanel(new BorderLayout());
                cell.setBackground(Color.BLACK);
                cell.setBorder(new LineBorder(Color.GRAY,1));
                cell.setPreferredSize(new Dimension(10,10));
                JLabel numLabel=new JLabel(String.valueOf(index),SwingConstants.CENTER);
                numLabel.setForeground(Color.WHITE);
                numLabel.setFont(new Font("Consolas",Font.PLAIN,12));
                cell.add(numLabel,BorderLayout.CENTER);
                cells[i][j]=cell;
                gridPanel.add(cell);
            }
        }
        JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.CENTER,10,8));
        buttonPanel.setBackground(new Color(30,30,30));
        JButton insertBtn=new JButton("Insert");
        JButton lookupBtn=new JButton("Lookup");
        JButton statsBtn=new JButton("Stats");
        JButton exitBtn=new JButton("Exit");
        styleButton(insertBtn);
        styleButton(lookupBtn);
        styleButton(statsBtn);
        styleButton(exitBtn);
        buttonPanel.add(insertBtn);
        buttonPanel.add(lookupBtn);
        buttonPanel.add(statsBtn);
        buttonPanel.add(exitBtn);
        add(scrollPane,BorderLayout.NORTH);
        add(gridPanel,BorderLayout.CENTER);
        add(buttonPanel,BorderLayout.SOUTH);
        insertBtn.addActionListener(e -> handleInsert());
        lookupBtn.addActionListener(e -> handleLookup());
        statsBtn.addActionListener(e -> showStats());
        exitBtn.addActionListener(e -> System.exit(0));
    }
    private void handleInsert(){
        String input=JOptionPane.showInputDialog(this,"Enter element to insert:");
        if(input!=null && !input.trim().isEmpty()){
            ArrayList<Integer> indices=getHashIndices(input);
            bloomFilter.insert(input);
            numElements++;
            StringBuilder sb=new StringBuilder();
            sb.append(numElements).append(". ").append(input).append(" → Indexes: ").append(indices);
            elementListModel.addElement(sb.toString());
            elementList.ensureIndexIsVisible(elementListModel.size()-1);
            updateGrid();
        }
    }
    private void handleLookup(){
        String input=JOptionPane.showInputDialog(this,"Enter element to lookup:");
        if(input!=null && !input.trim().isEmpty()){
            boolean found=bloomFilter.lookup(input);
            ArrayList<Integer> indices=getHashIndices(input);
            JOptionPane.showMessageDialog(this,
                    (found?"Possibly present":"Definitely not present")
                            +"\nChecked indexes: "+indices,
                    "Lookup Result",
                    found?JOptionPane.INFORMATION_MESSAGE:JOptionPane.WARNING_MESSAGE);
        }
    }
    private void showStats(){
        double fpr=bloomFilter.currentFalsePositiveRate(numElements);
        JOptionPane.showMessageDialog(this,
                "Inserted elements: "+numElements+
                        "\nEstimated false positive rate: "+String.format("%.2f",fpr)+"%",
                "Bloom Filter Stats",
                JOptionPane.INFORMATION_MESSAGE);
    }
    private void updateGrid(){
        BitSet bits=getBitSet(bloomFilter);
        for(int i=0;i<NUM_CELLS;i++){
            int row=i/GRID_SIZE;
            int col=i%GRID_SIZE;
            if(i<bloomFilter.size() && bits.get(i)){
                cells[row][col].setBackground(Color.GREEN);
            }
            else{
                cells[row][col].setBackground(Color.BLACK);
            }
        }
    }
    private ArrayList<Integer> getHashIndices(String item){
        ArrayList<Integer> indices=new ArrayList<>();
        try{
            java.lang.reflect.Field f1=BloomFilter.class.getDeclaredField("hashSet1");
            java.lang.reflect.Field f2=BloomFilter.class.getDeclaredField("hashSet2");
            java.lang.reflect.Field fs=BloomFilter.class.getDeclaredField("size");
            f1.setAccessible(true);
            f2.setAccessible(true);
            fs.setAccessible(true);
            java.util.function.Function<String,Integer>[] h1=
                    (java.util.function.Function<String,Integer>[])f1.get(bloomFilter);
            java.util.function.Function<String,Integer>[] h2=
                    (java.util.function.Function<String,Integer>[])f2.get(bloomFilter);
            int size=(int)fs.get(bloomFilter);
            for(int i=0;i<7;i++){
                int a=h1[i].apply(item);
                int b=h2[i].apply(item);
                int index=Math.abs((a+b)%size);
                indices.add(index);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return indices;
    }
    private BitSet getBitSet(BloomFilter<String> bf){
        try{
            java.lang.reflect.Field f=BloomFilter.class.getDeclaredField("bitArray");
            f.setAccessible(true);
            return (BitSet)f.get(bf);
        }
        catch(Exception e){
            e.printStackTrace();
            return new BitSet();
        }
    }
    private void styleButton(JButton btn){
        btn.setFocusPainted(false);
        btn.setBackground(new Color(60,60,60));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial",Font.BOLD,12));
        btn.setPreferredSize(new Dimension(90,30));
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            BloomFilterGUI gui=new BloomFilterGUI();
            gui.setVisible(true);
        });
    }
}

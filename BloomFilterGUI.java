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

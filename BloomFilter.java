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
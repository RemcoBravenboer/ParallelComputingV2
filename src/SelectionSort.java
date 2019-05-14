import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SelectionSort {
    private static final int availableProcessors = Runtime.getRuntime().availableProcessors();
    private static final int NUMBER_COUNT = 500;
    private static List<Integer> sortedList = new ArrayList<>();
    private static int[][] splitArray;
    private static List<Integer> lowestNumbers = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        List<Integer> numbers = Numbers.GenerateNumber(NUMBER_COUNT);

        class Lowest {
            synchronized int getLowest(int index) {
                int lowestInArray = Integer.MAX_VALUE;
                for (int i = 0; i < splitArray[index].length; i++) {
                    if (splitArray[index][i] < lowestInArray) {
                        lowestInArray = splitArray[index][i];
                    }
                }
                return lowestInArray;
            }
        }

        Lowest lowest = new Lowest();

        class SelectionSortThread extends Thread {
            private int splitArrayIndex;

            private SelectionSortThread(int splitArrayIndex) {
                this.splitArrayIndex = splitArrayIndex;
            }

            public void run() {
                lowestNumbers.add(lowest.getLowest(splitArrayIndex));
            }
        }

        long startingTime = System.currentTimeMillis();




        for (int i = 0; i < NUMBER_COUNT; i++) {
            splitArray = fillSplitArray(availableProcessors, numbers);
            lowestNumbers.clear();
            List<Thread> threads = new ArrayList<>();
            for (int j = 0; j < availableProcessors; j++) {
                if(splitArray[j] != null) {
                    threads.add(new SelectionSortThread(j));
                }
            }
            for (Thread thread : threads) {
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }
            System.out.println(i);
            int lowestInArray = getLowest(lowestNumbers);
            System.out.println(lowestInArray);
            numbers = swap(numbers, lowestInArray);
            sortedList.add(numbers.get(0));
            numbers.remove(0);
        }

        System.out.println("Sorted list: " + Arrays.toString(sortedList.toArray()));
        System.out.println(System.currentTimeMillis() - startingTime);
    }

    private static int getLowest(List<Integer> lowestNumbers) {
        int lowestInArray = Integer.MAX_VALUE;
        for (Integer lowestNumber : lowestNumbers) {
            if (lowestNumber < lowestInArray) {
                lowestInArray = lowestNumber;
            }
        }
        return lowestInArray;
    }

    private static List<Integer> swap(List<Integer> list, int lowest)
    {
        int n = list.size();
        for(int i = 0; i < n; i++)
        {
            if(list.get(i) == lowest) {
                Collections.swap(list, 0, i);
                return list;
            }
        }
        return null;
    }



    static int[][] fillSplitArray(int arrayAmount, List<Integer> listToUse) {
        if (listToUse.size() == 0) {
            return new int[0][0];
        }

        int splitLength = (int) Math.ceil((double) listToUse.size() / (double) arrayAmount);
        int[][] splits = new int[arrayAmount][];

        int j = 0;
        int k = 0;
        for (int i = 0; i < listToUse.size(); i++) {
            if (k == splitLength) {
                k = 0;
                j++;
            }
            if (splits[j] == null) {
                int remainingNumbers = listToUse.size() - i;
                splits[j] = new int[Math.min(remainingNumbers, splitLength)];
            }
            splits[j][k++] = listToUse.get(i);
        }
        return splits;
    };
}

package org.ashish.learning.Problem_Solving.Arrays;

public class BestTimeToBuyAndSellStockOptimal {
    static void main() {
        int[] arr = {7,1,5,3,6,4};
        int result = bestTimeToBuyAndSell(arr);
        System.out.println(result);
    }

    static int bestTimeToBuyAndSell(int[] prices){
        int maxProfit = 0;
        int min = Integer.MAX_VALUE;

        for(int i=0; i<prices.length; i++){
            if(prices[i] < min){
                min = prices [i];
            }

            int profit = prices[i] - min;
                if(profit > maxProfit){
                    maxProfit = profit;
                }
        }
        if(maxProfit > 0){
            return maxProfit;
        }
        return 0;
    }
}

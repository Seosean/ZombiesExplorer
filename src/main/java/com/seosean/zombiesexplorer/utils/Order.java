package com.seosean.zombiesexplorer.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Order {
    public static List<String> orderList = new ArrayList<>();

    public static void input(List<String> orderList) {
        if (Order.orderList.isEmpty()) {
            Order.orderList = orderList;
            return;
        }

        String head = "";

        for (int i = 0; i < orderList.size(); i++) {
            List<String> tempList = new ArrayList<>(orderList);
            if (!Order.orderList.contains(orderList.get(i))) {
                if (i == orderList.size() - 1) {
                    if (head.isEmpty()) {
                        Order.orderList.addAll(tempList);
                    } else {
                        int headIndex = orderList.indexOf(head);
                        Order.orderList.addAll(tempList.subList(headIndex + 1, orderList.size()));
                    }
                }
            } else {
                if (head.isEmpty()) {
                    head = orderList.get(i);
                    List<String> listAddOnHead = tempList.subList(0, i);
                    listAddOnHead.addAll(Order.orderList);
                    Order.orderList = listAddOnHead;
                } else {
                    int headIndex = orderList.indexOf(head);
                    int originalFootIndex = Order.orderList.indexOf(orderList.get(i));
                    List<String> listAddOnFoot = tempList.subList(headIndex + 1, i);
                    List<String> tempOriginalList = new ArrayList<>(Order.orderList);
                    List<String> newUpperOringalList = new ArrayList<>(tempOriginalList.subList(0, originalFootIndex));
                    List<String> newLowerOringalList = new ArrayList<>(tempOriginalList.subList(originalFootIndex, tempOriginalList.size()));
                    newUpperOringalList.addAll(listAddOnFoot);
                    newUpperOringalList.addAll(newLowerOringalList);
                    Order.orderList = newUpperOringalList;
                }
            }
        }

        HashMap<String, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < Order.orderList.size(); i++) {
            hashMap.put(Order.orderList.get(i), i);
        }

        Order.orderList = hashMap.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).collect(Collectors.toCollection(LinkedHashSet::new)).stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }
}

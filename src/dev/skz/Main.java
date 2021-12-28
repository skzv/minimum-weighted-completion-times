package dev.skz;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        List<Job> jobs = loadJobs();
        orderJobs(GreedyAlgorithm.RATIO, jobs);
        verifyOrder(GreedyAlgorithm.RATIO, jobs); // This sanity check verifies the metric is strictly decreasing
        System.out.println(computeCost(jobs));
    }

    public static List<Job> loadJobs() {
        String file = "input.txt";
        List<Job> jobs = new ArrayList<>();

        try (
                BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null) {
                int numJobs = Integer.parseInt(line);
                jobs = new ArrayList<>(numJobs);
            }
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(" ");
                int weight = Integer.parseInt(tokens[0]);
                int length = Integer.parseInt(tokens[1]);
                jobs.add(new Job(weight, length));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jobs;
    }

    public static long computeCost(List<Job> jobs) {
        int time = 0;
        long cost = 0;
        for (Job job : jobs) {
            time += job.length;
            cost += (long) time * job.weight;
        }
        return cost;
    }

    public static int computeDifferenceMetric(Job job) {
        return job.weight - job.length;
    }

    public static double computeRatioMetric(Job job) {
        return ((double) job.weight) / job.length;
    }

    public static void verifyOrder(GreedyAlgorithm greedyAlgorithm, List<Job> jobs) {
        List<Double> metrics;
        if (greedyAlgorithm == GreedyAlgorithm.DIFFERENCE) {
            metrics = jobs.stream().map(Main::computeDifferenceMetric).map(Double::valueOf).collect(Collectors.toList());
        } else {
            metrics = jobs.stream().map(Main::computeRatioMetric).collect(Collectors.toList());
        }

        double lastMetric = Double.MAX_VALUE;
        for (double metric : metrics) {
            if (metric > lastMetric) {
                throw new IllegalStateException("metric must be monotonically decreasing");
            }
            lastMetric = metric;
        }
    }

    public static void orderJobs(GreedyAlgorithm greedyAlgorithm, List<Job> jobs) {
        if (greedyAlgorithm == GreedyAlgorithm.DIFFERENCE) {
            jobs.sort((job1, job2) -> {
                int t1 = computeDifferenceMetric(job1);
                int t2 = computeDifferenceMetric(job2);
                if (t1 == t2) {
                    return job2.weight - job1.weight;
                }
                return t2 - t1;
            });
        } else {
            jobs.sort(Comparator.comparingDouble(Main::computeRatioMetric).reversed());
        }
    }

    public static class Job {

        int weight;
        int length;

        public Job(int weight, int length) {
            this.weight = weight;
            this.length = length;
        }
    }

    public enum GreedyAlgorithm {
        DIFFERENCE,
        RATIO
    }
}

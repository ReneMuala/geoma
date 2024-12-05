package org.descartes.geoma.engine;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class Navigator implements Cloneable {
    private final Target target;
    private final Model model;
    private Marker marker;
    private Navigator predecessor = null;
    private List<Navigator> successors = new ArrayList<>();

    public Navigator(Marker marker, Target target, Model model) {
        this.marker = marker.clone();
        this.target = target.clone();
        this.model = model;

        Point closestTargetPoint;
        Point closestMarkerPoint;
        double closestTargetPointDistance = Double.MAX_VALUE;
        double closestMarkerPointDistance = Double.MAX_VALUE;

        if(!model.shortPaths.isEmpty()){
            closestTargetPoint = model.shortPaths.get(0).first;
            closestTargetPointDistance = closestTargetPoint.distance(target.getOrigin());
            closestMarkerPoint = model.shortPaths.get(0).first;
            closestMarkerPointDistance = closestMarkerPoint.distance(marker.getOrigin());
        } else {
            return;
        }

        for (Line line : this.model.shortPaths) {
            if(closestMarkerPointDistance != 0){
                double distance = line.first.distance(marker.getOrigin());
                if(distance < closestMarkerPointDistance) {
                    closestMarkerPoint = line.first;
                    closestMarkerPointDistance = distance;
                }
                distance = line.second.distance(marker.getOrigin());
                if(distance < closestMarkerPointDistance) {
                    closestMarkerPoint = line.second;
                    closestMarkerPointDistance = distance;
                }
            }

            if(closestTargetPointDistance != 0){
                double distance = line.first.distance(target.getOrigin());
                if(distance < closestTargetPointDistance) {
                    closestTargetPoint = line.first;
                    closestTargetPointDistance = distance;
                }
                distance = line.second.distance(target.getOrigin());
                if(distance < closestTargetPointDistance) {
                    closestTargetPoint = line.second;
                    closestTargetPointDistance = distance;
                }
            }

            if(closestTargetPointDistance == 0 && closestMarkerPointDistance == 0){
                System.out.println("Found target and marker");
                break;
            }
        }

        this.target.setOrigin(closestTargetPoint);
        this.marker.setOrigin(closestMarkerPoint);
    }

    public boolean isOk(){
        return target.getOrigin().equals(marker.getOrigin());
    }

    public Path getPath(){
        Path path = new Path();
        Navigator it = this;
        while (it != null) {
            path.addPoint(it.marker.getOrigin());
            it = it.predecessor;
        } return path;
    }

    public long getSteps(){
        Navigator pred = predecessor;
        long steps = 0;
        while (pred != null) {
            steps++;
            pred = pred.predecessor;
        } return steps;
    }

    public double getDistance(){
        return Math.sqrt(Math.pow(marker.getOrigin().getX() - target.getOrigin().getX(), 2) + Math.pow(marker.getOrigin().getY() - target.getOrigin().getY(), 2));
    }

    public boolean isUnique(){
        Navigator pred = predecessor;
        while (pred != null) {
            if(pred.marker.getOrigin().equals(marker.getOrigin())){
                return false;
            }
            pred = pred.predecessor;
        } return true;
    }

    public List<Path> navigate(Counter counter) {
        List<Path> paths = new ArrayList<>();
        if(counter.isDone()){
            return paths;
        }
        successors.clear();
        List<Line> possibleShortPaths = model.getShortPaths().stream().filter(sp -> sp.first.equals(marker.getOrigin()) || sp.second.equals(marker.getOrigin())).toList();
        for (Line sp : possibleShortPaths) {
            Navigator successorCandidate = this.clone();
            successorCandidate.marker.setOrigin(sp.first.equals(marker.getOrigin()) ? sp.second : sp.first);
            if (successorCandidate.isUnique()) {
                successors.add(successorCandidate);
                if(successorCandidate.isOk()){
//                    System.out.println("Found: " + counter + " after " + successorCandidate.getSteps() + " steps");
                    counter.count();
                    paths.add(successorCandidate.getPath());
                    return paths;
                }
//                else {
//                    paths.addAll(successorCandidate.navigate(counter));
//                }
            }
        }
        successors.sort(Comparator.comparingDouble(Navigator::getDistance));
        if(!counter.isDone()){
            for (Navigator s : successors) {
                paths.addAll(s.navigate(counter));
            }
        };
//        successors.removeIf(successor -> !successor.navigate(requiredSolutions));
        return paths;
    }

    @Override
    public Navigator clone() {
        try {
            Navigator navigator = (Navigator) super.clone();
            navigator.successors= new ArrayList<>();
            navigator.marker = marker.clone();
            navigator.predecessor = this;
            return navigator;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

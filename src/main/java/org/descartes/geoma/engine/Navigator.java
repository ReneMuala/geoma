package org.descartes.geoma.engine;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Setter
@Getter
public class Navigator implements Cloneable {
    private final Target target;
    private final Model model;
    private Marker marker;
    private Navigator predecessor = null;
    private List<Navigator> successors = new ArrayList<>();
    private long successorsMaxBufferSize = 100;

    public Navigator(Marker marker, Target target, Model model) {
        this.marker = marker.clone();
        this.target = target.clone();
        this.model = model;

        Point closestTargetPoint;
        Point closestMarkerPoint;
        double closestTargetPointDistance;
        double closestMarkerPointDistance;

        if(!model.shortPaths.isEmpty()){
            closestTargetPoint = model.shortPaths.getFirst().first;
            closestTargetPointDistance = closestTargetPoint.distance(target.getOrigin());
            closestMarkerPoint = model.shortPaths.getFirst().first;
            closestMarkerPointDistance = closestMarkerPoint.distance(marker.getOrigin());
        } else {
            return;
        }

        for (Line line : this.model.shortPaths) {
            if(closestMarkerPointDistance != 0){
                double distance = line.distance(marker.getOrigin());
                if(distance < closestMarkerPointDistance) {
                    closestMarkerPoint = line.first.distance(marker.getOrigin()) > line.second.distance(marker.getOrigin()) ? line.second : line.first;
                    closestMarkerPointDistance = distance;
                }
            }

            if(closestTargetPointDistance != 0){
                double distance = line.distance(target.getOrigin());
                if(distance < closestTargetPointDistance) {
                    closestTargetPoint = line.first.distance(target.getOrigin()) > line.second.distance(target.getOrigin()) ? line.second : line.first;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Navigator navigator = (Navigator) o;
        return marker.getOrigin().equals(navigator.getMarker().getOrigin());
    }

    @Override
    public int hashCode() {
        return marker.getOrigin().toString().hashCode();
    }

    private List<Navigator> getSuccessors(){
        List<Navigator> result = new ArrayList<>();
        List<Line> sps = model.getShortPaths().stream().filter(sp -> sp.first.equals(marker.getOrigin()) || sp.second.equals(marker.getOrigin())).toList();
        for (Line sp : sps) {
            Navigator successorCandidate = this.clone();
            successorCandidate.marker.setOrigin(sp.first.equals(marker.getOrigin()) ? sp.second : sp.first);
            if(successorCandidate.isUnique()){
                result.add(successorCandidate);
            }
        }
        return result;
    }

    public List<Path> navigate(Counter counter) {
        List<Path> paths = new ArrayList<>();
        List<Navigator> navigators = new ArrayList<>();
        navigators.add(this);

        while(!counter.isDone()){
            List<Navigator> nextNavigators = new ArrayList<>();
            for(Navigator navigator : navigators.stream().toList()){
                if(navigator.isOk()) {
                    counter.count();
                    paths.add(navigator.getPath());
                    if(counter.isDone())
                        return paths;
                } else
                    nextNavigators.addAll(navigator.getSuccessors());
            }
            navigators = nextNavigators
                    .stream()
                    .sorted(Comparator.comparingDouble(Navigator::getDistance))
                    .limit(successorsMaxBufferSize)
                    .toList();
//            System.out.println("distance: " + navigators.get(0).getDistance() + " " + navigators.get(0).getMarker().getOrigin());
        }
        return paths;
    }

    @Deprecated(forRemoval = true)
    public List<Path> navigateDeeply(Counter counter) {
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
                    counter.count();
                    paths.add(successorCandidate.getPath());
                    return paths;
                }
            }
        }
        successors.sort(Comparator.comparingDouble(Navigator::getDistance));
        if(!counter.isDone()){
            for (Navigator s : successors) {
                paths.addAll(s.navigateDeeply(counter));
            }
        }
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

package org.descartes.geoma.engine;

import lombok.Getter;

import java.util.*;

@Getter
public class Model {
    // TODO: improve line class hash so that this can be a set instead of a list
    final List<Line> shortPaths = new ArrayList<>();
    public Model(List<Path> paths, List<Obstacle> obstacles) {
        for(Path path : paths){
            for(int i = 0; i < path.getPoints().size() - 1; i++) {
                Point start = path.getPoints().get(i);
                Point end = path.getPoints().get(i + 1);
                if(start.equals(end)) continue;
                shortPaths.add(new Line(start, end));
            }
        }
        final Set<Line> tempShortPaths = new HashSet<>();

        for(Line line : shortPaths){
            for(Line subLine : shortPaths){
                if(line == subLine) continue;
                Point interception = line.getInterceptionPoint(subLine);
                if(interception == null) continue;
               /*
                Given two lines
                A -----> C
                B -----> D


                if they collide, then we should infer:
                    A -----> B
                    A -----> D
                    C -----> B
                    C -----> D
                 */
                tempShortPaths.add(new Line(line.first, interception));
                tempShortPaths.add(new Line(interception, subLine.first));

                tempShortPaths.add(new Line(line.first, interception));
                tempShortPaths.add(new Line(interception, subLine.second));

                tempShortPaths.add(new Line(line.second, interception));
                tempShortPaths.add(new Line(interception, subLine.first));

                tempShortPaths.add(new Line(line.second, interception));
                tempShortPaths.add(new Line(interception, subLine.second));
            }
        }
        shortPaths.addAll(tempShortPaths);
        Iterator<Line> item = shortPaths.iterator();
        while(item.hasNext()){
            Line line = item.next();
            if(shortPaths.stream().filter(e -> e.equals(line)).count() > 1 || obstacles.stream().anyMatch(o -> line.intersects(o.getOrigin(), o.getLength()))) {
                item.remove();
            }
        }
    }
}

package io.github.nejc92.sy.utilities;

import com.rits.cloning.Cloner;
import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.board.Connection;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.Multigraph;

import java.util.List;
import java.util.stream.IntStream;

public class BoardGraphGenerator {

    private final String fileName;
    private UndirectedGraph<Integer, Connection> graph;

    public BoardGraphGenerator(String fileName) {
        this.fileName = fileName;
    }

    public UndirectedGraph<Integer, Connection> generateGraph() {
        graph = new Multigraph<>(Connection.class);
        BoardFileParser boardFileParser = new BoardFileParser(fileName);
        List<List<Action>> positionActions = boardFileParser.getParsedData();
        fillGraphFromPositionActions(positionActions);
        return graph;
    }

    private void fillGraphFromPositionActions(List<List<Action>> positionActions) {
        addVerticesToGraph(positionActions);
        addEdgesToGraph(positionActions);
    }

    private void addVerticesToGraph(List<List<Action>> positionActions) {
        IntStream.range(0, positionActions.size())
                .forEach(position -> graph.addVertex(position + 1));
    }

    private void addEdgesToGraph(List<List<Action>> positionActions) {
        IntStream.range(0, positionActions.size())
                .forEach(position -> generateEdgesFromActions(position + 1, positionActions.get(position)));
    }

    private void generateEdgesFromActions(int position, List<Action> actions) {
        actions.stream()
                .forEach(action -> graph.addEdge(position, action.getDestination(),
                        new Connection(position, action.getDestination(), action.getTransportation())));
    }

    public UndirectedGraph<Integer, Connection> generateSeekersGraph(UndirectedGraph<Integer, Connection> graph) {
        Cloner cloner = new Cloner();
        UndirectedGraph<Integer, Connection> newGraph = cloner.deepClone(graph);
        graph.edgeSet().stream()
                .filter(connection -> connection.isTransportation(Connection.Transportation.BLACK_FARE))
                .forEach(newGraph::removeEdge);
        return newGraph;
    }
}
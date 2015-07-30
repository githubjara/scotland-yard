package io.github.nejc92.sy.players;

import io.github.nejc92.mcts.MctsDomainAgent;
import io.github.nejc92.sy.game.Action;
import io.github.nejc92.sy.game.board.Connection;
import io.github.nejc92.sy.game.State;

public abstract class Player implements MctsDomainAgent<State> {

    public enum Operator {
        HUMAN, COMPUTER
    }

    public enum Type {
        HIDER, SEEKER
    }

    private final Operator operator;
    private final Type type;
    private int taxiTickets;
    private int busTickets;
    private int undergroundTickets;

    protected Player(Operator operator, Type type, int taxiTickets, int busTickets, int undergroundTickets) {
        this.operator = operator;
        this.type = type;
        this.taxiTickets = taxiTickets;
        this.busTickets = busTickets;
        this.undergroundTickets = undergroundTickets;
    }

    public int getTaxiTickets() {
        return taxiTickets;
    }

    public int getBusTickets() {
        return busTickets;
    }

    public int getUndergroundTickets() {
        return undergroundTickets;
    }

    public boolean isHider() {
        return type == Type.HIDER;
    }

    public boolean isSeeker() {
        return type == Type.SEEKER;
    }

    public boolean isHuman() {
        return operator == Operator.HUMAN;
    }

    public boolean hasTaxiTickets() {
        return taxiTickets > 0;
    }

    public boolean hasBusTickets() {
        return busTickets > 0;
    }

    public boolean hasUndergroundTickets() {
        return undergroundTickets > 0;
    }

    public void removeTicket(Connection.Transportation transportation) {
        switch (transportation) {
            case TAXI:
                taxiTickets--;
                break;
            case BUS:
                busTickets--;
                break;
            case UNDERGROUND:
                undergroundTickets--;
        }
    }

    protected void addTicket(Connection.Transportation transportation) {
        switch (transportation) {
            case TAXI:
                taxiTickets++;
                break;
            case BUS:
                busTickets++;
                break;
            case UNDERGROUND:
                undergroundTickets++;
        }
    }

    @Override
    public final State getTerminalStateByPerformingSimulationFromState(State state) {
        state.setSimulationOn();
        while (!state.isTerminal()) {
            Action randomAction;
            if (state.currentPlayerIsHider())
                randomAction = getHidersActionFromStatesAvailableActionsForSimulation(state);
            else
                randomAction = getSeekersActionFromStatesAvailableActionsForSimulation(state);
            if (randomAction != null) {
                state.performActionForCurrentAgent(randomAction);
            }
            else
                state.skipCurrentAgent();
        }
        return state;
    }

    protected abstract Action getHidersActionFromStatesAvailableActionsForSimulation(State state);

    protected abstract Action getSeekersActionFromStatesAvailableActionsForSimulation(State state);
}
package com.smartpigs.pig.client;

import com.smartpigs.enums.OccupantType;
import com.smartpigs.model.Occupant;
import com.smartpigs.model.Pig;
import com.smartpigs.pig.server.PigServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ShelterInformer {

    private final Pig sender;
    private final List<List<Occupant>> neighbors;
    private final NeighborMovedListener listener;

    public ShelterInformer(final Pig sender, final List<List<Occupant>> neighbors,
            final NeighborMovedListener listener) {
        this.sender = sender;
        this.neighbors = neighbors;
        this.listener = listener;
    }

    /**
     * Informs all pig {@link #neighbors} of a {@link #sender} pig to take shelter.
     * <p>
     * The pig {@link #neighbors} will then find an appropriate empty cell to move to, if any.
     */
    public void inform() {
        neighbors.stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .filter(occupant -> occupant.getOccupantType() == OccupantType.PIG)
                .forEach(occupant -> inform((Pig) occupant));
    }

    private void inform(final Pig pig) {
        try {
            if (PigServer.connect(pig).takeShelter(sender)) {
                listener.moved(pig);
            }
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }

    public interface NeighborMovedListener {
        void moved(final Pig neighbor);
    }
}

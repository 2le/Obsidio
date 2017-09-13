package com.benberi.cadesim.game.entity.vessel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.benberi.cadesim.GameContext;
import com.benberi.cadesim.game.entity.Entity;
import com.benberi.cadesim.game.entity.projectile.CannonBall;
import com.benberi.cadesim.game.entity.vessel.move.MoveAnimationStructure;
import com.benberi.cadesim.game.entity.vessel.move.MovePhase;
import com.benberi.cadesim.game.entity.vessel.move.MoveType;
import com.benberi.cadesim.game.entity.vessel.move.VesselMoveTurn;
import com.benberi.cadesim.util.OrientationLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a vessel abstraction
 */
public abstract class Vessel extends Entity {

    /**
     * The name of this vessel player
     */
    private String name;

    /**
     * If the vessel is moving
     */
    private boolean isMoving;

    /**
     * The rotation index of the vessel
     */
    private int rotationIndex;

    /**
     * The target index for the rotation in animation
     */
    private int rotationTargetIndex = -1;

    /**
     * The animation handler
     */
    private VesselAnimationVector animation;

    /**
     * Current performing move
     */
    private VesselMovementAnimation currentPerformingMove;

    /**
     * The turn animation structure
     */
    private MoveAnimationStructure structure = new MoveAnimationStructure();

    /**
     * The current turn
     */
    private VesselMoveTurn turn;

    /**
     * The last finished phase
     */
    private MovePhase finishedPhase;

    private int numberOfMoves;

    protected TextureRegion shootSmoke;

    private boolean isSmoking;

    private int smokeTicks;

    /**
     * The cannon balls that were shoot
     */
    private List<CannonBall> cannonballs = new ArrayList<CannonBall>();
    private int moveDelay;

    public Vessel(GameContext context, String name, int x, int y) {
        super(context);
        this.name = name;
        this.setPosition(x, y);
        turn = new VesselMoveTurn();
    }

    public void setMovePhase(MovePhase phase) {
        this.finishedPhase = phase;
    }

    public MovePhase getMovePhase() {
        return finishedPhase;
    }

    public MoveAnimationStructure getStructure() {
        return structure;
    }

    public int getNumberOfMoves() {
        return numberOfMoves;
    }

    public void setNumberOfMoves(int moves) {
        this.numberOfMoves = moves;
    }

    /**
     * Starts to perform a given move
     * @param move The move to perform
     */
    public void performMove(VesselMovementAnimation move) {
        Vector2 start = new Vector2(this.getX(), this.getY());
        Vector2 currentAnimationLocation = start.cpy();
        this.currentPerformingMove = move;

        Vector2 inbetween = null;
        if (move != VesselMovementAnimation.MOVE_FORWARD) {
            // Get the inbetween block by using forward
            inbetween = new Vector2(start.x + MoveType.FORWARD.getIncrementXForRotation(rotationIndex),
                    start.y + MoveType.FORWARD.getIncrementYForRotation(rotationIndex));
            this.rotationTargetIndex = move.getRotationTargetIndex(rotationIndex);
        }

        Vector2 end = new Vector2(start.x + move.getIncrementXForRotation(rotationIndex),
                start.y + move.getIncrementYForRotation(rotationIndex));

        Vector2 linear = start.cpy();
        this.animation = new VesselAnimationVector(start, inbetween, end, currentAnimationLocation, linear);
        setMoving(true);
    }


    public String getName() {
        return this.name;
    }

    public boolean isSmoking() {
        return this.isSmoking;
    }

    public void tickMoveDelay() {
        moveDelay -= 100 * Gdx.graphics.getDeltaTime();
        if (moveDelay <= 0) {
            moveDelay = -1;
        }
    }

    public int getMoveDelay() {
        return this.moveDelay;
    }

    public void tickSmoke() {
        if (smokeTicks >= 5) {
            shootSmoke.setRegion(shootSmoke.getRegionX() + 40, 0, 40, 30);
            if (shootSmoke.getRegionX() > shootSmoke.getTexture().getWidth()) {
                isSmoking = false;
                shootSmoke.setRegion(0, 0, 40, 30);
            }
            smokeTicks = 0;
        }
        else {
            smokeTicks += 100 * Gdx.graphics.getDeltaTime();
        }
    }

    /**
     * Gets the animation handler for vessel
     * @return {@link #animation}
     */
    public VesselAnimationVector getAnimation() {
        return this.animation;
    }

    /**
     * Gets the current performing move
     * @return {@link #currentPerformingMove}
     */
    public VesselMovementAnimation getCurrentPerformingMove() {
        return this.currentPerformingMove;
    }

    /**
     * @return The target rotation index for animation
     */
    public int getRotationTargetIndex() {
        return this.rotationTargetIndex;
    }
    /**
     * If the ship currently performing move animation or not
     * @param flag If moving or not
     */
    public void setMoving(boolean flag) {
        this.isMoving = flag;
    }

    /**
     * If the ship is moving or not
     * @return TRUE if moving FALSE if not
     */
    public boolean isMoving() {
        return this.isMoving;
    }

    /**
     * Gets the current rotation index
     * @return {{@link #rotationIndex}}
     */
    public int getRotationIndex() {
        return this.rotationIndex;
    }

    public TextureRegion getShootSmoke() {
        return shootSmoke;
    }

    /**
     * Ticks up to next rotation
     */
    public void tickRotation() {
        if (rotationIndex == rotationTargetIndex) {
            return;
        }
        if (currentPerformingMove == VesselMovementAnimation.TURN_LEFT) {
            this.rotationIndex--;
        }
        else if (currentPerformingMove == VesselMovementAnimation.TURN_RIGHT) {
            this.rotationIndex++;
        }

        if (rotationIndex > 15) {
            rotationIndex = 0;
        }
        else if (rotationIndex < 0) {
            rotationIndex = 14;
        }

        this.updateRotation();
    }

    /**
     * Sets rotation index
     * @param index The new index
     */
    public void setRotationIndex(int index) {
        this.rotationIndex = index;
        this.updateRotation();
    }

    /**
     * Updates sprite region to new rotation
     */
    private void updateRotation() {
        this.setOrientationLocation(this.rotationIndex);
        OrientationLocation location = this.getOrientationLocation();
        try {
            this.setRegion(location.getX(), location.getY(), location.getWidth(), location.getHeight());
        }
        catch(NullPointerException e) {
            System.err.println(rotationIndex + " " + rotationTargetIndex);
        }
    }

    private Vector2 getClosestLeftCannonCollide() {
        switch (rotationIndex) {
            case 2:
                for (int i = 1; i < 4; i++) {
                    Vessel vessel = getContext().getEntities().getVesselByPosition(getX(), getY() + i);
                    if (vessel != null) {
                        return new Vector2(getX(), getY() + i);
                    }
                }
                return new Vector2(getX(), getY() + 3);
            case 6:
                for (int i = 1; i < 4; i++) {
                    Vessel vessel = getContext().getEntities().getVesselByPosition(getX() + i, getY());
                    if (vessel != null) {
                        return new Vector2(getX() + i, getY());
                    }
                }
                return new Vector2(getX() + 3, getY());
            case 10:
                for (int i = 1; i < 4; i++) {
                    Vessel vessel = getContext().getEntities().getVesselByPosition(getX(), getY() - i);
                    if (vessel != null) {
                        return new Vector2(getX(), getY() - i);
                    }
                }
                return new Vector2(getX(), getY() - 3);
            case 14:
                for (int i = 1; i < 4; i++) {
                    Vessel vessel = getContext().getEntities().getVesselByPosition(getX() - i, getY());
                    if (vessel != null) {
                        return new Vector2(getX() - i, getY());
                    }
                }
                return new Vector2(getX() - 3, getY());
        }
        return new Vector2(getX(), getY());
    }


    public Vector2 getClosestRightCannonCollide() {
        switch (rotationIndex) {
            case 2:
                for (int i = 1; i < 4; i++) {
                    Vessel vessel = getContext().getEntities().getVesselByPosition(getX(), getY() - i);
                    if (vessel != null) {
                        return new Vector2(getX(), getY() - i);
                    }
                }
                return new Vector2(getX(), getY() - 3);
            case 6:
                for (int i = 1; i < 4; i++) {
                    Vessel vessel = getContext().getEntities().getVesselByPosition(getX() - i, getY());
                    if (vessel != null) {
                        return new Vector2(getX() - i, getY());
                    }
                }
                return new Vector2(getX() - 3, getY());
            case 10:
                for (int i = 1; i < 4; i++) {
                    Vessel vessel = getContext().getEntities().getVesselByPosition(getX(), getY() + i);
                    if (vessel != null) {
                        return new Vector2(getX(), getY() + i);
                    }
                }
                return new Vector2(getX(), getY() + 3);
            case 14:
                for (int i = 1; i < 4; i++) {
                    Vessel vessel = getContext().getEntities().getVesselByPosition(getX() + i, getY());
                    if (vessel != null) {
                        return new Vector2(getX() + i, getY());
                    }
                }
                return new Vector2(getX() + 3, getY());
        }
        return new Vector2(getX(), getY());
    }

    /**
     * Maximum amount of cannons
     */
    public abstract int getMaxCannons();

    public abstract CannonBall createCannon(GameContext ctx, Vessel source, Vector2 target);

    public abstract VesselMoveType getMoveType();

    public List<CannonBall> getCannonballs() {
        return this.cannonballs;
    }

    public void performLeftShoot(int leftShoots) {
        if (leftShoots == 1) {
            Vector2 target = getClosestLeftCannonCollide();
            CannonBall ball = createCannon(getContext(), this, target);
            if (getContext().getEntities().getVesselByPosition(target.x, target.y) != null) {
                ball.setExplodeOnReach(true);
            }
            cannonballs.add(ball);
        }
        else if (leftShoots == 2) {
            Vector2 target = getClosestLeftCannonCollide();

            CannonBall ball1 = createCannon(getContext(), this, target);
            CannonBall ball2 = createCannon(getContext(), this, target);

            if (getContext().getEntities().getVesselByPosition(target.x, target.y) != null) {
                ball1.setExplodeOnReach(true);
                ball2.setExplodeOnReach(true);
            }
            ball2.setReleased(false);

            ball1.setSubcannon(ball2);
            cannonballs.add(ball1);
            cannonballs.add(ball2);
        }
        shootSmoke.setRegion(0, 0, 40, 30);
        isSmoking = true;

    }

    public void performRightShoot(int rightShoots) {
        if (rightShoots == 1) {
            Vector2 target = getClosestRightCannonCollide();
            CannonBall ball = createCannon(getContext(), this, target);
            if (getContext().getEntities().getVesselByPosition(target.x, target.y) != null) {
                ball.setExplodeOnReach(true);
            }
            cannonballs.add(ball);
        }
        else if (rightShoots == 2) {
            Vector2 target = getClosestRightCannonCollide();

            CannonBall ball1 = createCannon(getContext(), this, target);
            CannonBall ball2 = createCannon(getContext(), this, target);
            if (getContext().getEntities().getVesselByPosition(target.x, target.y) != null) {
                ball1.setExplodeOnReach(true);
                ball2.setExplodeOnReach(true);
            }
            ball2.setReleased(false);

            ball1.setSubcannon(ball2);
            cannonballs.add(ball1);
            cannonballs.add(ball2);
        }
        shootSmoke.setRegion(0, 0, 40, 30);
        isSmoking = true;
    }

    public void setMoveDelay() {
        this.moveDelay = 70;
    }

}

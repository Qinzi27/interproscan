package uk.ac.ebi.interpro.scan.management.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Abstract class for executing a Step.
 *
 * @author Phil Jones
 * @version $Id$
 * @since 1.0-SNAPSHOT
 */
public abstract class StepExecution implements Serializable {

    private Long id;

    protected StepInstance stepInstance;

    private StepExecutionState state = StepExecutionState.NEW_STEP_EXECUTION;

    private Date createdTime;

    private Date submittedTime;

    private Date completedTime;

    protected StepExecution(Long id, StepInstance stepInstance, StepExecutionState state) {
        this.stepInstance = stepInstance;
        this.state = state;
        this.id = id;
        createdTime = new Date();
    }

    public void setStepInstance(StepInstance stepInstance) {
        this.stepInstance = stepInstance;
    }

    public void setState(StepExecutionState state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public StepInstance getStepInstance() {
        return stepInstance;
    }

    public StepExecutionState getState() {
        return state;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public Date getSubmittedTime() {
        return submittedTime;
    }

    public Date getCompletedTime() {
        return completedTime;
    }

    /**
     * This method is called to execute the action that the StepExecution must perform.
     * This method should typically perform its activity in a try / catch / finally block
     * that sets the state of the step execution appropriately.
     *
     * Note that the implementation DOES have access to the protected stepInstance,
     * and from their to the protected Step, to allow it to access parameters for execution.
     */
    public abstract void execute();

    public void submit(){
        if (state != StepExecutionState.NEW_STEP_EXECUTION){
            throw new IllegalStateException ("Attempting to submit a StepExecution to a queue, which is not in state 'NEW_STEP_EXECUTION'.");
        }
        state = StepExecutionState.STEP_EXECUTION_SUBMITTED;
        submittedTime = new Date();
    }

    /**
     * Called by the execute() method implementation to indicate successful completion.
     */
    protected void completeSuccessfully(){
        if (state != StepExecutionState.STEP_EXECUTION_SUBMITTED){
            throw new IllegalStateException("Try to set the state of this StepExecution to 'STEP_EXECUTION_SUCCESSFUL', however it is currently in state "+ state);
        }
        state = StepExecutionState.STEP_EXECUTION_SUCCESSFUL;
        completedTime = new Date();
    }

    /**
     * Called by the execute() method implementation to indicate a failure of execution.
     */
    protected void fail(){
        if (state != StepExecutionState.STEP_EXECUTION_SUBMITTED){
            throw new IllegalStateException("Try to set the state of this StepExecution to 'STEP_EXECUTION_FAILED', however it is currently in state "+ state);
        }
        state = StepExecutionState.STEP_EXECUTION_FAILED;
        completedTime = new Date();
    }
}

public class StepByStepListener implements RecursionListener {
    private final GraphService graphService;

    public StepByStepListener() {
        this.graphService = new GraphService();
    }

    @Override
    public void onCall(int value, String parentId, String callId) {
        graphService.addNode(value, parentId, callId);
        graphService.highlight(callId);
        graphService.saveStepImage();
    }
}

package com.leroy.core.testrail.run;

import com.leroy.core.testrail.TestRailClient;
import lombok.Data;
import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Run {

    /**
     * Close test runs
     *
     * @param projectId - project id
     * @throws Exception
     */
    private void closeTestRuns(long projectId) throws Exception {
        int i = 0;
        while (i < 100) {
            i++;
            JSONArray runs = TestRailClient.getRuns(projectId, false);
            for (Object run : runs) {
                long id = (long) ((JSONObject) run).get("id");
                if (id < 45000) {
                    try {
                        System.out.println("Id=" + id);
                        TestRailClient.closeRun(id);
                    } catch (Exception err) {
                        String s = "";
                    }
                }
            }
        }
    }

    private static void buildCoverageMatrix(long projectId, long suiteId) throws Exception {
        List<String> fullReport = new ArrayList<>();
        List<String> shortReport = new ArrayList<>();
        SectionTree tree = new SectionTree();
        JSONArray sections = TestRailClient.getSections(projectId, suiteId);

        CasesCountData totalCaseCountDepth_0 = new CasesCountData();
        CasesCountData TOTAL_CASE_COUNT = new CasesCountData();

        fullReport.add(CasesCountData.fullVersionHeader);
        shortReport.add(CasesCountData.shortVersionHeader);

        String fullText = null;
        String shortText = null;

        SectionData sectionData;
        String sectionToAddInFile = null;
        boolean flag = false;
        for (Object sectionObj : sections) {
            JSONObject sectionJson = (JSONObject) sectionObj;
            sectionData = new SectionData();
            sectionData.setDepth((long) sectionJson.get("depth"));
            sectionData.setId((long) sectionJson.get("id"));
            sectionData.setName((String) sectionJson.get("name"));
            sectionData.setParentId((Long) sectionJson.get("parent_id"));
            if (flag && sectionData.getDepth() == 0)
                break;
            if (sectionData.getName().equals("Regression Tests"))
                flag = true;
            if (!flag)
                continue;
            if (sectionData.getDepth() == 1) {
                sectionToAddInFile = sectionData.getName();
                if (fullText != null) {
                    fullReport.add(fullText + "\n");
                }
                if (shortText != null) {
                    shortReport.add(shortText + "\n");
                }
                TOTAL_CASE_COUNT.plusCase(totalCaseCountDepth_0);
                totalCaseCountDepth_0 = new CasesCountData();
                sectionData.setTotalCasesCount(totalCaseCountDepth_0);
            }
            CasesCountData casesCount = new CasesCountData();
            System.out.println("Section: " + sectionData.getName());
            JSONArray casesJsonArray = TestRailClient.getCases(projectId, suiteId, sectionData.getId());
            if (casesJsonArray.size() > 0) {
                for (Object caseObj : casesJsonArray) {
                    casesCount.plusCase((Long) ((JSONObject) caseObj).get("custom_status"));
                }
            }
            totalCaseCountDepth_0.plusCase(casesCount);
            fullText = sectionToAddInFile + ";" + totalCaseCountDepth_0.getFullStatusesString();
            shortText = sectionToAddInFile + ";" + totalCaseCountDepth_0.getShortStatusesString();
            sectionData.setSelfCasesCount(casesCount);
            tree.addSection(sectionData);
        }
        if (fullText != null) {
            fullReport.add(fullText + "\n");
        }
        if (shortText != null) {
            shortReport.add(shortText + "\n");
        }
        TOTAL_CASE_COUNT.plusCase(totalCaseCountDepth_0);

        fullReport.add("ВСЕГО:;" + TOTAL_CASE_COUNT.getFullStatusesString());
        shortReport.add("ВСЕГО:;" + TOTAL_CASE_COUNT.getShortStatusesString());

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(
                "CoverageMatrix_full.csv"), Charset.forName("cp1251"))) {
            for (String str : fullReport) {
                writer.write(str);
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(
                "CoverageMatrix_short.csv"), Charset.forName("cp1251"))) {
            for (String str : shortReport) {
                writer.write(str);
            }
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        buildCoverageMatrix(10L, 258L);
        //buildCoverageMatrix(16L, 4378L);
    }

    private interface ICaseStatus {
        int EMPTY_STATUS = -1;
        int NEED_TO_UPDATE_STEPS = 1;
        int READY_FOR_REVIEW = 6;
        int READY_FOR_AUTOMATION = 2;
        int NEED_TO_REAUTOMATE = 3;
        int MANUAL = 4;
        int AUTOMATED = 5;
        int OBSOLETE = 7;
    }

    @Data
    private static class SectionData {
        private Long id;
        private Long depth;
        private Long parentId;
        private String name;
        private CasesCountData selfCasesCount;
        private CasesCountData totalCasesCount;
    }

    @Data
    private static class CasesCountData {
        private int emptyStatus;
        private int needToUpdateSteps;
        private int readyForReview;
        private int readyForAutomation;
        private int needToReAutomate;
        private int manual;
        private int automated;
        private int obsolete;

        final static String shortVersionHeader = ";Ручной;Автоматизирован;Остальное;\n";
        final static String fullVersionHeader = ";Need to update steps;Ready for review;Ready for automation;Need to re-automate;Manual;Automated;Obsolete;No Status\n";

        public String getShortStatusesString() {
            return manual + ";" +
                    (needToReAutomate + automated) + ";" +
                    (emptyStatus + needToUpdateSteps + readyForReview + readyForAutomation + obsolete);
        }

        public String getFullStatusesString() {
            return needToUpdateSteps + ";" + readyForReview + ";" + readyForAutomation + ";" + needToReAutomate + ";" +
                    manual + ";" + automated + ";" + obsolete + ";" + emptyStatus;
        }

        public void plusCase(CasesCountData casesCount) {
            if (casesCount == null)
                return;
            needToUpdateSteps += casesCount.getNeedToUpdateSteps();
            readyForReview += casesCount.getReadyForReview();
            readyForAutomation += casesCount.getReadyForAutomation();
            needToReAutomate += casesCount.getNeedToReAutomate();
            manual += casesCount.getManual();
            automated += casesCount.getAutomated();
            obsolete += casesCount.getObsolete();
            emptyStatus += casesCount.getEmptyStatus();
        }

        public void plusCase(Long status) {
            int iStatus;
            if (status == null)
                iStatus = -1;
            else
                iStatus = status.intValue();
            switch (iStatus) {
                case ICaseStatus.NEED_TO_UPDATE_STEPS:
                    needToUpdateSteps++;
                    break;
                case ICaseStatus.READY_FOR_REVIEW:
                    readyForReview++;
                    break;
                case ICaseStatus.READY_FOR_AUTOMATION:
                    readyForAutomation++;
                    break;
                case ICaseStatus.NEED_TO_REAUTOMATE:
                    needToReAutomate++;
                    break;
                case ICaseStatus.MANUAL:
                    manual++;
                    break;
                case ICaseStatus.AUTOMATED:
                    automated++;
                    break;
                case ICaseStatus.OBSOLETE:
                    obsolete++;
                    break;
                case ICaseStatus.EMPTY_STATUS:
                    emptyStatus++;
                    break;
            }
        }

        public int getTotalCount() {
            return needToUpdateSteps + readyForAutomation + readyForReview + needToReAutomate +
                    manual + automated + obsolete + emptyStatus;
        }
    }

    private static class TreeNode {

        @Getter
        private SectionData data;

        private TreeNode parent;
        @Getter
        private List<TreeNode> children;

        public TreeNode getParent() {
            return parent;
        }

        public TreeNode getParent(long depth) {
            if (data.getDepth() == depth)
                return this;
            else if (data.getDepth() > depth)
                return parent.getParent(depth);
            else
                return null;
        }

        public TreeNode(SectionData data) {
            this.data = data;
            this.children = new LinkedList<>();
        }

        public TreeNode addChild(SectionData child) {
            TreeNode childNode = new TreeNode(child);
            childNode.parent = this;
            this.children.add(childNode);
            return childNode;
        }

        public TreeNode findChildById(long id) {
            for (TreeNode child : children) {
                if (child.getData().getId() == id)
                    return child;
            }
            return null;
        }
    }

    private static class SectionTree {

        private TreeNode root;
        private TreeNode currentNode;

        public SectionTree() {
            root = new TreeNode(new SectionData());
            currentNode = root;
        }

        public void addSection(SectionData sectionData) throws Exception {
            if (sectionData.getDepth() == 0 /*&& !sectionData.getName().equals("Regression Tests")*/)
                return;
            if (sectionData.getDepth() == 1 ) {
                currentNode = root.addChild(sectionData);
            } else if (sectionData.getDepth().equals(currentNode.getData().getDepth())) {
                TreeNode parent = currentNode.getParent();
                if (!sectionData.getParentId().equals(parent.getData().getId()))
                    throw new Exception("Что-то пошло не так, у родителя не тот id, что ожидался");
                currentNode = parent.addChild(sectionData);
            } else if (sectionData.getDepth() > currentNode.getData().getDepth()) {
                if (!sectionData.getParentId().equals(currentNode.getData().getId()))
                    throw new Exception("Эта секция должна быть дочерней для currentNode (1)");
                currentNode = currentNode.addChild(sectionData);
            } else if (sectionData.getDepth() < currentNode.getData().getDepth()) {
                currentNode = currentNode.getParent(sectionData.getDepth() - 1);
                if (!sectionData.getParentId().equals(currentNode.getData().getId()))
                    throw new Exception("Эта секция должна быть дочерней для currentNode (2)");
                currentNode = currentNode.addChild(sectionData);
            } else {
                throw new Exception("Это как? Что за ситуация?");
            }

        }

    }

}

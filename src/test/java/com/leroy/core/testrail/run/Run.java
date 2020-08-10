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
import java.nio.charset.StandardCharsets;
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
        SectionTree tree = new SectionTree();
        JSONArray sections = TestRailClient.getSections(projectId, suiteId);

        CasesCount totalCaseCountDepth_0 = null;
        //new FileWriter("CoverageMatrix.txt", false)
        try (Writer writer = new OutputStreamWriter(new FileOutputStream("CoverageMatrix.csv"), StandardCharsets.UTF_8)) {
            writer.write(";Нужно обновить тест кейс;Готов для проверки;Готов для автоматизации;Нужно переавтоматизировать;Ручной;Автоматизирован;Устарел;Без статуса\n");
            String text = null;

            int iCount = 0;
            SectionData sectionData = null;
            String sectionName_0 = null;
            for (Object sectionObj : sections) {
                iCount++;
                JSONObject sectionJson = (JSONObject) sectionObj;
                sectionData = new SectionData();
                sectionData.setDepth((long) sectionJson.get("depth"));
                sectionData.setId((long) sectionJson.get("id"));
                sectionData.setName((String) sectionJson.get("name"));
                sectionData.setParentId((Long) sectionJson.get("parent_id"));
                if (sectionData.getDepth() == 0) {
                    sectionName_0 = sectionData.getName();
                    if (text != null)
                        writer.write(text + "\n");
                    totalCaseCountDepth_0 = new CasesCount();
                    sectionData.setTotalCasesCount(totalCaseCountDepth_0);
                }
                CasesCount casesCount = new CasesCount();
                System.out.println("Section: " + sectionData.getName());
                JSONArray casesJsonArray = TestRailClient.getCases(projectId, suiteId, sectionData.getId());
                if (casesJsonArray.size() > 0) {
                    for (Object caseObj : casesJsonArray) {
                        casesCount.plusCase((Long) ((JSONObject) caseObj).get("custom_status"));
                    }
                }
                totalCaseCountDepth_0.plusCase(casesCount);
                text = sectionName_0 + ";" + totalCaseCountDepth_0.toString();
                sectionData.setSelfCasesCount(casesCount);
                tree.addSection(sectionData);
                //if (iCount > 60)
                //    break;
            }
            if (text != null)
                writer.write(text + "\n");

            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        String s = "";
    }

    public static void main(String[] args) throws Exception {
        buildCoverageMatrix(10L, 258L);
    }

    public interface ICaseStatus {
        int EMPTY_STATUS = -1;
        int NEED_TO_UPDATE_STEPS = 1; // +
        int READY_FOR_REVIEW = 6;
        int READY_FOR_AUTOMATION = 2; // +
        int NEED_TO_REAUTOMATE = 3;
        int MANUAL = 4; // +
        int AUTOMATED = 5; // +
        int OBSOLETE = 7;
    }

    @Data
    public static class SectionData {
        private Long id;
        private Long depth;
        private Long parentId;
        private String name;
        private CasesCount selfCasesCount;
        private CasesCount totalCasesCount;
    }

    @Data
    public static class CasesCount {
        private int emptyStatus;
        private int needToUpdateSteps;
        private int readyForReview;
        private int readyForAutomation;
        private int needToReAutomate;
        private int manual;
        private int automated;
        private int obsolete;

        @Override
        public String toString() {
            return needToUpdateSteps + ";" + readyForReview + ";" + readyForAutomation + ";" + needToReAutomate + ";" +
                    manual + ";" + automated + ";" + obsolete + ";" + emptyStatus;
        }

        public void plusCase(CasesCount casesCount) {
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

    public static class TreeNode {

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

    public static class SectionTree {

        private TreeNode root;
        private TreeNode currentNode;

        public SectionTree() {
            root = new TreeNode(new SectionData());
            currentNode = root;
        }

//        public void reCalculateNodeCasesCount() {
//            for (TreeNode childLevel_1 : root.children) {
//                for (TreeNode childLevel_2 : childLevel_1.children) {
//                    CasesCount chl_2 = new CasesCount();
//                    for (TreeNode childLevel_3 : childLevel_2.children) {
//                        chl_2.plusCase(childLevel_3.getData().getSelfCasesCount());
//                    }
//                }
//            }
//        }

        public void addSection(SectionData sectionData) throws Exception {
            if (sectionData.getDepth() == 0) {
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

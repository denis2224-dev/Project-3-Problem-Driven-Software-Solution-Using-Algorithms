package com.unischeduler.service.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Result of applying Welsh-Powell graph coloring to exams.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExamColoringResultDTO implements Serializable {

    private int examCount;
    private int colorCount;
    private int conflictEdgeCount;
    private boolean conflictFree;
    private List<ExamColoringEntryDTO> entries = new ArrayList<>();

    public int getExamCount() {
        return examCount;
    }

    public void setExamCount(int examCount) {
        this.examCount = examCount;
    }

    public int getColorCount() {
        return colorCount;
    }

    public void setColorCount(int colorCount) {
        this.colorCount = colorCount;
    }

    public int getConflictEdgeCount() {
        return conflictEdgeCount;
    }

    public void setConflictEdgeCount(int conflictEdgeCount) {
        this.conflictEdgeCount = conflictEdgeCount;
    }

    public boolean isConflictFree() {
        return conflictFree;
    }

    public void setConflictFree(boolean conflictFree) {
        this.conflictFree = conflictFree;
    }

    public List<ExamColoringEntryDTO> getEntries() {
        return entries;
    }

    public void setEntries(List<ExamColoringEntryDTO> entries) {
        this.entries = entries;
    }
}

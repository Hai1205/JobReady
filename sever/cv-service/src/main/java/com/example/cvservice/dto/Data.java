package com.example.cvservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

/**
 * Generic data container for all service responses
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Data {
    // CV related data
    private Object cv;
    private List<?> cvs;
    private Object experience;
    private List<?> experiences;
    private Object education;
    private List<?> educations;
    private List<?> skills;

    // Pagination and stats
    private Object pagination;
    private Map<String, Object> stats;

    // Generic data container for any other service-specific data
    private Map<String, Object> additionalData;

    public Data() {
    }

    public Object getCv() {
        return cv;
    }

    public void setCv(Object cv) {
        this.cv = cv;
    }

    public List<?> getCvs() {
        return cvs;
    }

    public void setCvs(List<?> cvs) {
        this.cvs = cvs;
    }

    public Object getExperience() {
        return experience;
    }

    public void setExperience(Object experience) {
        this.experience = experience;
    }

    public List<?> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<?> experiences) {
        this.experiences = experiences;
    }

    public Object getEducation() {
        return education;
    }

    public void setEducation(Object education) {
        this.education = education;
    }

    public List<?> getEducations() {
        return educations;
    }

    public void setEducations(List<?> educations) {
        this.educations = educations;
    }

    public List<?> getSkills() {
        return skills;
    }

    public void setSkills(List<?> skills) {
        this.skills = skills;
    }

    public Object getPagination() {
        return pagination;
    }

    public void setPagination(Object pagination) {
        this.pagination = pagination;
    }

    public Map<String, Object> getStats() {
        return stats;
    }

    public void setStats(Map<String, Object> stats) {
        this.stats = stats;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }
}
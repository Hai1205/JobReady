package com.example.userservice.services.apis;

import com.example.userservice.dtos.PlanDto;
import com.example.userservice.dtos.requests.plan.*;
import com.example.userservice.dtos.response.Response;
import com.example.userservice.entities.*;
import com.example.userservice.exceptions.OurException;
import com.example.userservice.mappers.PlanMapper;
import com.example.userservice.repositories.plan.*;
import com.example.cloudinarycommon.CloudinaryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlanApi extends BaseApi {

    private final SimplePlanRepository simplePlanRepository;
    private final PlanQueryRepository planQueryRepository;
    private final PlanCommandRepository planCommandRepository;
    private final PlanMapper planMapper;
    private final ObjectMapper objectMapper;

    public PlanApi(
            SimplePlanRepository simplePlanRepository,
            PlanQueryRepository planQueryRepository,
            PlanCommandRepository planCommandRepository,
            PlanMapper planMapper,
            CloudinaryService cloudinaryService) {
        this.simplePlanRepository = simplePlanRepository;
        this.planQueryRepository = planQueryRepository;
        this.planCommandRepository = planCommandRepository;
        this.planMapper = planMapper;
        this.objectMapper = new ObjectMapper();
    }

    public Response createPlan(String dataJson) {
        logger.info("Creating new plan");
        Response response = new Response();

        try {
           CreatePlanRequest request = objectMapper.readValue(dataJson, CreatePlanRequest.class);
            String type = request.getType();
            String title = request.getTitle();
            Long price = request.getPrice();
            String currency = request.getCurrency();
            String period = request.getPeriod();
            String description = request.getDescription();
            List<String> features = request.getFeatures();
            Boolean isRecommended = request.getIsRecommended();
            Boolean isPopular = request.getIsPopular();

            PlanDto savedPlanDto = handleCreatePlan(type, title, price, currency, period, description, features,
                    isRecommended, isPopular);

            response.setStatusCode(201);
            response.setMessage("Plan created successfully");
            response.setPlan(savedPlanDto);
            logger.info("Plan creation completed successfully: {}", title);
            return response;
        } catch (OurException e) {
            logger.error("Plan creation failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Plan creation failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public PlanDto handleCreatePlan(String type, String title, Long price, String currency, String period,
            String description, List<String> features, Boolean isRecommended, Boolean isPopular) {
        try {
            logger.info("Creating plan with title: {}", title);

            if (title == null || title.isEmpty()) {
                logger.warn("Attempted to create plan with empty title");
                throw new OurException("Plan title is required", 400);
            }

            if (price == null || price < 0) {
                logger.warn("Invalid price for plan: {}", title);
                throw new OurException("Price must be greater than or equal to 0", 400);
            }

            UUID planId = UUID.randomUUID();
            String featuresJson = objectMapper.writeValueAsString(features);

            int result = planCommandRepository.insertPlan(
                    planId,
                    type,
                    title,
                    price,
                    currency != null ? currency : "VND",
                    period != null ? period : "month",
                    description,
                    featuresJson,
                    isRecommended != null ? isRecommended : false,
                    isPopular != null ? isPopular : false);

            if (result == 0) {
                throw new OurException("Failed to create plan", 500);
            }

            Plan savedPlan = planQueryRepository.findPlanById(planId)
                    .orElseThrow(() -> new OurException("Plan created but not found", 500));

            logger.info("Plan created successfully with ID: {}", planId);
            return planMapper.toDto(savedPlan);
        } catch (OurException e) {
            logger.error("Error in handleCreatePlan: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleCreatePlan: {}", e.getMessage(), e);
            throw new OurException("Failed to create plan", 500);
        }
    }

    public Response getAllPlans() {
        logger.info("Getting all plans");
        Response response = new Response();

        try {
            List<PlanDto> plans = handleGetAllPlans();

            response.setStatusCode(200);
            response.setMessage("Plans retrieved successfully");
            response.setPlanList(plans);
            logger.info("Plans retrieval completed successfully. Count: {}", plans.size());
            return response;
        } catch (OurException e) {
            logger.error("Plans retrieval failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Plans retrieval failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public List<PlanDto> handleGetAllPlans() {
        try {
            logger.info("Fetching all plans");
            List<Plan> plans = planQueryRepository.findAllPlansList();
            return plans.stream()
                    .map(planMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Unexpected error in handleGetAllPlans: {}", e.getMessage(), e);
            throw new OurException("Failed to retrieve plans", 500);
        }
    }

    public Response deletePlan(UUID planId) {
        logger.info("Deleting plan: {}", planId);
        Response response = new Response();

        try {
            handleDeletePlan(planId);

            response.setStatusCode(200);
            response.setMessage("Plan deleted successfully");
            logger.info("Plan deletion completed successfully: {}", planId);
            return response;
        } catch (OurException e) {
            logger.error("Plan deletion failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Plan deletion failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public void handleDeletePlan(UUID planId) {
        try {
            logger.info("Deleting plan with ID: {}", planId);

            if (!simplePlanRepository.existsByPlanId(planId)) {
                logger.warn("Plan not found: {}", planId);
                throw new OurException("Plan not found", 404);
            }

            int result = planCommandRepository.deletePlanById(planId);
            if (result == 0) {
                throw new OurException("Failed to delete plan", 500);
            }

            logger.info("Plan deleted successfully: {}", planId);
        } catch (OurException e) {
            logger.error("Error in handleDeletePlan: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleDeletePlan: {}", e.getMessage(), e);
            throw new OurException("Failed to delete plan", 500);
        }
    }

    public Response getPlanById(UUID planId) {
        logger.info("Getting plan by ID: {}", planId);
        Response response = new Response();

        try {
            PlanDto planDto = handleGetPlanById(planId);

            response.setStatusCode(200);
            response.setMessage("Plan retrieved successfully");
            response.setPlan(planDto);
            logger.info("Plan retrieval completed successfully: {}", planId);
            return response;
        } catch (OurException e) {
            logger.error("Plan retrieval failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Plan retrieval failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public PlanDto handleGetPlanById(UUID planId) {
        try {
            logger.info("Fetching plan with ID: {}", planId);
            Plan plan = planQueryRepository.findPlanById(planId)
                    .orElseThrow(() -> new OurException("Plan not found", 404));
            return planMapper.toDto(plan);
        } catch (OurException e) {
            logger.error("Error in handleGetPlanById: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleGetPlanById: {}", e.getMessage(), e);
            throw new OurException("Failed to retrieve plan", 500);
        }
    }

    public Response updatePlan(UUID planId, String dataJson) {
        logger.info("Updating plan: {}", planId);
        Response response = new Response();

        try {
            UpdatePlanRequest request = objectMapper.readValue(dataJson, UpdatePlanRequest.class);
            String type = request.getType();
            String title = request.getTitle();
            Long price = request.getPrice();
            String currency = request.getCurrency();
            String period = request.getPeriod();
            String description = request.getDescription();
            List<String> features = request.getFeatures();
            Boolean isRecommended = request.getIsRecommended();
            Boolean isPopular = request.getIsPopular();
            PlanDto updatedPlanDto = handleUpdatePlan(planId, type, title, price, currency, period, description,
                    features, isRecommended, isPopular);

            response.setStatusCode(200);
            response.setMessage("Plan updated successfully");
            response.setPlan(updatedPlanDto);
            logger.info("Plan update completed successfully: {}", planId);
            return response;
        } catch (OurException e) {
            logger.error("Plan update failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("Plan update failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public PlanDto handleUpdatePlan(UUID planId, String type, String title, Long price, String currency,
            String period, String description, List<String> features,
            Boolean isRecommended, Boolean isPopular) {
        try {
            logger.info("Updating plan with ID: {}", planId);

            planQueryRepository.findPlanById(planId)
                    .orElseThrow(() -> new OurException("Plan not found", 404));

            int result = planCommandRepository.updatePlanAllFields(
                    planId,
                    type,
                    title,
                    price,
                    currency,
                    period,
                    description,
                    features,
                    isRecommended,
                    isPopular);

            if (result == 0) {
                throw new OurException("Failed to update plan", 500);
            }

            Plan updatedPlan = planQueryRepository.findPlanById(planId)
                    .orElseThrow(() -> new OurException("Plan updated but not found", 500));

            logger.info("Plan updated successfully: {}", planId);
            return planMapper.toDto(updatedPlan);
        } catch (OurException e) {
            logger.error("Error in handleUpdatePlan: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleUpdatePlan: {}", e.getMessage(), e);
            throw new OurException("Failed to update plan", 500);
        }
    }
}
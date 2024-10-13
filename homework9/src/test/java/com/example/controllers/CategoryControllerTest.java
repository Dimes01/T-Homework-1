package com.example.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the {@link CategoryController}
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getAllCategories_return200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/places/categories"))
                .andExpect(status().isOk());
    }

    @Test
    public void getCategoryById_existedId_return200() throws Exception {
        mockMvc.perform(post("/api/v1/places/categories/{0}", "1")
                        .content("{\"id\":1,\"slug\":\"slug1\",\"name\":\"name1\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/places/categories/{0}", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void getCategoryById_notExistedId_return400() throws Exception {
        mockMvc.perform(get("/api/v1/places/categories/{0}", "-1"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void postCreateCategory_return200() throws Exception {
        mockMvc.perform(post("/api/v1/places/categories/{0}", "1")
                        .content("{\"id\":1,\"slug\":\"slug1\",\"name\":\"name1\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void putUpdateCategory_existedId_return200() throws Exception {
        String newCategory = "{\"id\":1,\"slug\":\"newSlug1\",\"name\":\"name1\"}";

        mockMvc.perform(post("/api/v1/places/categories/{0}", "1")
                        .content(newCategory)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/v1/places/categories/{0}", "1")
                        .content(newCategory)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void putUpdateCategory_notExistedId_return400() throws Exception {
        mockMvc.perform(put("/api/v1/places/categories/{0}", "-1")
                        .content("{\"id\":1,\"slug\":\"slug1\",\"name\":\"name1\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void deleteCategory_existedId_return200() throws Exception {
        mockMvc.perform(post("/api/v1/places/categories/{0}", "1")
                        .content("{\"id\":1,\"slug\":\"slug1\",\"name\":\"name1\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/places/categories/{0}", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCategory_notExistedId_return400() throws Exception {
        mockMvc.perform(delete("/api/v1/places/categories/{0}", "1"))
                .andExpect(status().is4xxClientError());
    }
}



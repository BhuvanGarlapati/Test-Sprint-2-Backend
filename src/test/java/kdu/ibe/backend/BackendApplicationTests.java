package kdu.ibe.backend;

import kdu.ibe.backend.dto.request.DateRangeRequestDto;
import kdu.ibe.backend.models.Configuration;
import kdu.ibe.backend.services.ConfigurationService;
import kdu.ibe.backend.services.PropertyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class BackendApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ConfigurationService configurationService;

	@MockBean
	private PropertyService propertyService;

	@Test
	public void testGetMinimumNightRate_Success() throws Exception {
		DateRangeRequestDto dateRange = new DateRangeRequestDto();
		dateRange.setStartDate("2024-03-01");
		dateRange.setEndDate("2024-03-04");

		// Create an ArgumentCaptor to capture the parameters passed to propertyService.getMinimumNightRate
		ArgumentCaptor<String> startDateCaptor = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> endDateCaptor = ArgumentCaptor.forClass(String.class);

		// Mocking the behavior of propertyService.getMinimumNightRate
		given(propertyService.getMinimumNightRate(
				any(String.class),
				any(String.class),
				eq(10L)))
				.willAnswer(invocation -> {
					String startDate = invocation.getArgument(0);
					String endDate = invocation.getArgument(1);
					Map<String, Double> minNightRates = getMockMinimumNightRates();
					Map<String, Double> convertedRates = new HashMap<>();
					LocalDate startLocalDate = LocalDate.parse(startDate);
					LocalDate endLocalDate = LocalDate.parse(endDate);
					for (LocalDate date = startLocalDate; date.isBefore(endLocalDate.plusDays(1)); date = date.plusDays(1)) {
						convertedRates.put(date.toString(), minNightRates.getOrDefault(date.toString(), 0.0));
					}
					return convertedRates;
				});

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/calendar/10")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"startDate\":\"2024-03-01\",\"endDate\":\"2024-03-04\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.2024-03-01").value(100.0))
				.andExpect(jsonPath("$.2024-03-02").value(120.0))
				.andExpect(jsonPath("$.2024-03-03").value(120.0))
				.andExpect(jsonPath("$.2024-03-04").value(130.0));

		verify(propertyService).getMinimumNightRate(startDateCaptor.capture(), endDateCaptor.capture(), eq(10L));
		assertEquals("2024-03-01", startDateCaptor.getValue());
		assertEquals("2024-03-04", endDateCaptor.getValue());
	}

	private Map<String, Double> getMockMinimumNightRates() {
		Map<String, Double> minNightRates = new HashMap<>();
		minNightRates.put("2024-03-01", 100.0);
		minNightRates.put("2024-03-02", 120.0);
		minNightRates.put("2024-03-03", 120.0);
		minNightRates.put("2024-03-04", 130.0);
		return minNightRates;
	}

	@Test
	void testGetConfigurationByPropertyId_EmptyData() throws Exception {
		given(configurationService.getConfigurationById(1L, 10L)).willReturn(getMockEmptyPropertyData());

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/configuration/property")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id\":1,\"propertyId\":10}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.property").isEmpty())
				.andExpect(jsonPath("$.global").isEmpty());
	}

	private Map<String, Object> getMockEmptyPropertyData() {
		return Map.of("property", new HashMap<>(), "global", new HashMap<>());
	}

	@Test
	public void testUpdateConfigurationData_Success() throws Exception {
		Configuration updatedConfiguration = new Configuration();
		updatedConfiguration.setId(1L);
		updatedConfiguration.setData(getMockConfigurationData());
		given(configurationService.updateConfigurationData(1L, updatedConfiguration)).willReturn(updatedConfiguration);

		mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/configuration/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"id\":1,\"data\":{\"global\":{},\"property\":{\"10\":{\"landing_page\":{}}}}}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.data.property.10.landing_page").exists());
	}

	private Map<String, Object> getMockConfigurationData() {
		return Map.of("global", new HashMap<>(), "property", Map.of("10", Map.of("landing_page", new HashMap<>())));
	}

}



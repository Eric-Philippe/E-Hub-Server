package org.ericp.ehub.server.utils

import org.ericp.ehub.server.repository.ToBuyLinkRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class IllustrationExtractorTest {

    private lateinit var toBuyLinkRepository: ToBuyLinkRepository
    private lateinit var extractor: IllustrationExtractor

    @BeforeEach
    fun setUp() {
        toBuyLinkRepository = mock(ToBuyLinkRepository::class.java)
        `when`(toBuyLinkRepository.findAllUrlAndIllustrationUrl()).thenReturn(emptyList())
        extractor = IllustrationExtractor(toBuyLinkRepository)
    }

    @Test
    fun `test illustration extractor with valid urls`() {
        val urls = listOf(
            "https://amzn.eu/d/6cuEfEK",
            "https://amzn.eu/d/fLCiIom"
        )
        val result = extractor.extract(urls)
        System.out.println(result.values)
        assertNotNull(result, "The result should not be null")
    }

    @Test
    fun `test illustration extractor with empty list`() {
        val urls = emptyList<String>()
        val result = extractor.extract(urls)
        assertNotNull(result, "The result should not be null for an empty list")
        assertTrue(result.isEmpty(), "The result should be an empty list")
    }
}

package net.jeikobu.doraipitto.processor

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import net.jeikobu.doraipitto.model.GuildConfig
import net.jeikobu.doraipitto.model.ReplacementDefinition

class MessageProcessorTest :
    BehaviorSpec({
        val sut = MessageProcessor()
        val guildConfig =
            GuildConfig(
                guildId = 1L,
                replacementDefinitions =
                    listOf(
                        ReplacementDefinition(hostnamesToReplace = listOf("twitter.com", "x.com"), replacement = "https://twitter.host"),
                        ReplacementDefinition(
                            hostnamesToReplace = listOf("reddit.com", "redditmedia.com", "redd.it"),
                            replacement = "https://reddit.host",
                        ),
                        ReplacementDefinition(hostnamesToReplace = listOf("instagram.com"), replacement = "https://instagram.host"),
                    ),
            )

        context("MessageProcessor should find Twitter URLs") {
            Given("a message with X.com URL") {
                val testMessage = "patrzcie na to XDDDD https://x.com/KinoAlertPL/status/1942235861766324324 pojebane"
                When("MessageProcessor processes it") {
                    val result = sut.getMessagesToSend(guildConfig, testMessage)
                    Then("MessageProcessor should return the correct result") {
                        result shouldBe listOf("https://twitter.host/KinoAlertPL/status/1942235861766324324")
                    }
                }
            }

            Given("a message with twitter.com URL") {
                val testMessage = "patrzcie na to XDDDD https://twitter.com/KinoAlertPL/status/1942235861766324324 pojebane"
                When("MessageProcessor processes it") {
                    val result = sut.getMessagesToSend(guildConfig, testMessage)
                    Then("MessageProcessor should return the correct result") {
                        result shouldBe listOf("https://twitter.host/KinoAlertPL/status/1942235861766324324")
                    }
                }
            }

            Given("a message with both X.com and twitter.com URLs") {
                val testMessage =
                    "patrzcie na to XDDDD https://twitter.com/KinoAlertPL/status/1942235861766324324 " +
                        "https://x.com/DiscussingFilm/status/1938024759305912791 pojebane"
                When("MessageProcessor processes it") {
                    val result = sut.getMessagesToSend(guildConfig, testMessage)
                    Then("MessageProcessor should return the correct result") {
                        result shouldBe
                            listOf(
                                "https://twitter.host/KinoAlertPL/status/1942235861766324324",
                                "https://twitter.host/DiscussingFilm/status/1938024759305912791",
                            )
                    }
                }
            }
        }

        context("MessageProcessor should find Instagram URLs") {
            Given("a message with instagram.com URL") {
                val testMessage = "patrzcie na to XDDDD https://www.instagram.com/misfit_printing/reel/DLyiZAdxYsN/ pojebane"
                When("MessageProcessor processes it") {
                    val result = sut.getMessagesToSend(guildConfig, testMessage)
                    Then("MessageProcessor should return the correct result") {
                        result shouldBe listOf("https://instagram.host/misfit_printing/reel/DLyiZAdxYsN/")
                    }
                }
            }
        }

        context("MessageProcessor should find Reddit URLs") {
            Given("a message with reddit.com URL") {
                val testMessage = "patrzcie na to XDDDD https://www.reddit.com/r/OfficeChairs/comments/1iuniv7/prosim_accis_pro/ pojebane"
                When("MessageProcessor processes it") {
                    val result = sut.getMessagesToSend(guildConfig, testMessage)
                    Then("MessageProcessor should return the correct result") {
                        result shouldBe listOf("https://reddit.host/r/OfficeChairs/comments/1iuniv7/prosim_accis_pro/")
                    }
                }
            }

            Given("a message with redditmedia.com URL") {
                val testMessage =
                    "patrzcie na to XDDDD " +
                        "https://www.redditmedia.com/r/OfficeChairs/comments/1iuniv7/prosim_accis_pro/ pojebane"
                When("MessageProcessor processes it") {
                    val result = sut.getMessagesToSend(guildConfig, testMessage)
                    Then("MessageProcessor should return the correct result") {
                        result shouldBe listOf("https://reddit.host/r/OfficeChairs/comments/1iuniv7/prosim_accis_pro/")
                    }
                }
            }

            Given("a message with redd.it URL") {
                val testMessage = "patrzcie na to XDDDD https://redd.it/r/OfficeChairs/comments/1iuniv7/prosim_accis_pro/ pojebane"
                When("MessageProcessor processes it") {
                    val result = sut.getMessagesToSend(guildConfig, testMessage)
                    Then("MessageProcessor should return the correct result") {
                        result shouldBe listOf("https://reddit.host/r/OfficeChairs/comments/1iuniv7/prosim_accis_pro/")
                    }
                }
            }

            Given("a message with all URLs") {
                val testMessage =
                    "patrzcie na to XDDDD https://redd.it/r/OfficeChairs/comments/1iuniv7/prosim_accis_pro/ " +
                        "https://www.reddit.com/r/OfficeChairs/comments/19dwmg4/ergohuman_gen2_elite/ XDDD " +
                        "https://redditmedia.com/r/linuxquestions/comments/1838nte/vlc_media_player_alternatives_for_linux/ pojebane"
                When("MessageProcessor processes it") {
                    val result = sut.getMessagesToSend(guildConfig, testMessage)
                    Then("MessageProcessor should return the correct result") {
                        result shouldContainAll
                            listOf(
                                "https://reddit.host/r/OfficeChairs/comments/1iuniv7/prosim_accis_pro/",
                                "https://reddit.host/r/OfficeChairs/comments/19dwmg4/ergohuman_gen2_elite/",
                                "https://reddit.host/r/linuxquestions/comments/1838nte/vlc_media_player_alternatives_for_linux/",
                            )
                    }
                }
            }
        }

        context("MessageProcessor should find all URLs regardless of type in one message") {
            Given("a message with reddit.com URL") {
                val testMessage =
                    "patrzcie na to XDDDD https://www.reddit.com/r/OfficeChairs/comments/1iuniv7/prosim_accis_pro/ https://www.instagram.com/misfit_printing/reel/DLyiZAdxYsN/ https://twitter.com/KinoAlertPL/status/1942235861766324324 pojebane"
                When("MessageProcessor processes it") {
                    val result = sut.getMessagesToSend(guildConfig, testMessage)
                    Then("MessageProcessor should return the correct result") {
                        result shouldContainAll
                            listOf(
                                "https://reddit.host/r/OfficeChairs/comments/1iuniv7/prosim_accis_pro/",
                                "https://instagram.host/misfit_printing/reel/DLyiZAdxYsN/",
                                "https://twitter.host/KinoAlertPL/status/1942235861766324324",
                            )
                    }
                }
            }
        }
    })

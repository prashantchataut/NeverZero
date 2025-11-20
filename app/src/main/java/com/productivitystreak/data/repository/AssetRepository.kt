package com.productivitystreak.data.repository

import com.productivitystreak.data.model.Asset
import com.productivitystreak.data.model.AssetCategory
import com.productivitystreak.data.model.AssetTest
import com.productivitystreak.data.model.AssetTestQuestion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AssetRepository {

    private val assetsFlow = MutableStateFlow(sampleAssets())

    fun observeAssets(): Flow<List<Asset>> = assetsFlow.asStateFlow()

    fun getAssetById(id: String): Asset? = assetsFlow.value.firstOrNull { it.id == id }

    fun markCertified(id: String): Asset? {
        val current = assetsFlow.value
        val updated = current.map { asset ->
            if (asset.id == id) asset.copy(certified = true) else asset
        }
        assetsFlow.value = updated
        return updated.firstOrNull { it.id == id }
    }

    private fun sampleAssets(): List<Asset> {
        val list = mutableListOf<Asset>()

        list += Asset(
            id = "psychology_1",
            title = "The 2-Minute Reframe",
            category = AssetCategory.PSYCHOLOGY_TRICKS,
            content = """\
Use this micro-protocol when you feel resistance to a task.\n\n1. Name the resistance in one sentence.\n2. Ask: \"What story am I telling myself?\"\n3. Replace it with a neutral, fact-based statement.\n4. Decide on the smallest next physical action (30–60 seconds).\n\nYour goal is not to feel motivated. Your goal is to remove cognitive friction so that starting becomes obvious.""".trimIndent(),
            test = AssetTest(
                questions = listOf(
                    AssetTestQuestion(
                        id = "psychology_1_q1",
                        prompt = "What is the primary goal of the 2-Minute Reframe?",
                        options = listOf(
                            "To feel highly motivated",
                            "To remove cognitive friction and make starting obvious",
                            "To plan the entire project in detail",
                            "To track time spent on a task"
                        ),
                        correctIndex = 1
                    ),
                    AssetTestQuestion(
                        id = "psychology_1_q2",
                        prompt = "What is the final step in the protocol?",
                        options = listOf(
                            "Write a detailed to-do list",
                            "Share the task with a friend",
                            "Decide on the smallest next physical action",
                            "Estimate how long the task will take"
                        ),
                        correctIndex = 2
                    )
                ),
                passingScore = 2
            ),
            xpValue = 10
        )

        list += Asset(
            id = "psychology_2",
            title = "Implementation Intentions",
            category = AssetCategory.PSYCHOLOGY_TRICKS,
            content = """\
Convert vague goals into \"If-Then\" statements.\n\nExample:\n- Goal: \"Read more.\"\n- If-Then: \"If it is 10:00 pm and I am in bed, then I read 5 pages.\"\n\nThe power comes from pre-deciding your behavior in a specific situation instead of relying on willpower in the moment.""".trimIndent(),
            test = AssetTest(
                questions = listOf(
                    AssetTestQuestion(
                        id = "psychology_2_q1",
                        prompt = "Implementation Intentions primarily reduce which requirement?",
                        options = listOf(
                            "Time",
                            "Willpower at decision time",
                            "Money",
                            "Energy expenditure"
                        ),
                        correctIndex = 1
                    )
                ),
                passingScore = 1
            ),
            xpValue = 10
        )

        list += Asset(
            id = "memory_1",
            title = "The 3-Point Recall",
            category = AssetCategory.MEMORY_TECHNIQUES,
            content = """\
When reading or listening, force your brain to compress information into three points.\n\n1. After a section, pause for 30 seconds.\n2. Write down three bullet points in your own words.\n3. Compare them with the original and adjust.\n\nThis is a fast way to turn passive exposure into active encoding.""".trimIndent(),
            xpValue = 8
        )

        list += Asset(
            id = "memory_2",
            title = "Spacing in Practice",
            category = AssetCategory.MEMORY_TECHNIQUES,
            content = """\
Use short, distributed reviews instead of long cram sessions.\n\nSchedule:\n- Day 0: Learn the concept.\n- Day 1: 2-minute review.\n- Day 3: 2-minute review.\n- Day 7: 2-minute review.\n\nEach review should be recall-based: test yourself before re-reading.""".trimIndent(),
            xpValue = 8
        )

        list += Asset(
            id = "negotiation_1",
            title = "Silence After the Number",
            category = AssetCategory.NEGOTIATION_SCRIPTS,
            content = """\
When you state a number (salary, price, budget), stop talking.\n\nWhy it works:\n- Reduces nervous justifications.\n- Forces the other side to respond to the anchor.\n\nPractice in low-stakes situations so silence feels normal, not threatening.""".trimIndent(),
            xpValue = 10
        )

        list += Asset(
            id = "negotiation_2",
            title = "Label the Emotion",
            category = AssetCategory.NEGOTIATION_SCRIPTS,
            content = """\
When tension rises, label what you observe.\n\nExamples:\n- \"It sounds like the timeline feels tight.\"\n- \"It seems this budget is higher than expected.\"\n\nAccurate labels reduce defensiveness and move the conversation back to problem-solving.""".trimIndent(),
            xpValue = 10
        )

        list += Asset(
            id = "marketing_1",
            title = "Before-After-Bridge Snapshot",
            category = AssetCategory.MARKETING_MENTAL_MODELS,
            content = """\
Describe the present, the desired future, and the bridge.\n\nStructure:\n- Before: One sentence on the current friction.\n- After: One sentence on life without that friction.\n- Bridge: One sentence on how your product moves them there.\n\nKeep each part specific and concrete.""".trimIndent(),
            xpValue = 10
        )

        list += Asset(
            id = "marketing_2",
            title = "Single Outcome Landing Page",
            category = AssetCategory.MARKETING_MENTAL_MODELS,
            content = """\
Design pages around one critical action.\n\nChecklist:\n- One primary CTA visible without scrolling.\n- Every section supports that action.\n- Remove links that pull visitors sideways.\n\nClarity beats cleverness for busy professionals.""".trimIndent(),
            xpValue = 10
        )

        list += Asset(
            id = "book_1",
            title = "Deep Work – Core Idea",
            category = AssetCategory.BOOK_SUMMARIES,
            content = """\
\"Deep work\" is sustained focus on cognitively demanding tasks without distraction.\n\nOperationalize it by:\n- Scheduling deep work blocks on your calendar.\n- Protecting them with clear rules (no messaging, no email).\n- Ending with a short review of what moved forward.""".trimIndent(),
            xpValue = 12
        )

        list += Asset(
            id = "book_2",
            title = "Atomic Habits – Habit Loop",
            category = AssetCategory.BOOK_SUMMARIES,
            content = """\
The habit loop: cue → craving → response → reward.\n\nUpgrades:\n- Make cues obvious.\n- Make responses small and executable in under two minutes.\n- Make rewards immediate and satisfying (even if symbolic).""".trimIndent(),
            xpValue = 12
        )

        list += Asset(
            id = "psychology_3",
            title = "The 90-Second Rule",
            category = AssetCategory.PSYCHOLOGY_TRICKS,
            content = """\
Most intense emotions peak and decay within ~90 seconds if you do not re-fuel them with new thoughts.\n\nProtocol:\n- Notice the surge.\n- Breathe and observe for 90 seconds.\n- Delay decisions until the wave passes.\n\nThis keeps long-term priorities safe from short-term spikes.""".trimIndent(),
            xpValue = 8
        )

        list += Asset(
            id = "memory_3",
            title = "Name-Occupation Link",
            category = AssetCategory.MEMORY_TECHNIQUES,
            content = """\
When meeting people, connect their name to what they do.\n\nExample:\n- \"Sarah – strategy lawyer\"\n- \"Vikram – vision architect\"\n\nSay the pair out loud once in the conversation to solidify the link.""".trimIndent(),
            xpValue = 6
        )

        list += Asset(
            id = "negotiation_3",
            title = "Best Alternative Check (BATNA)",
            category = AssetCategory.NEGOTIATION_SCRIPTS,
            content = """\
Before negotiating, list your best alternative if this deal fails.\n\nThen ask:\n- What makes this offer clearly better than my alternative?\n- At what point is walking away rational?\n\nYou negotiate from a position of clarity, not anxiety.""".trimIndent(),
            xpValue = 12
        )

        list += Asset(
            id = "marketing_3",
            title = "Concrete Credibility",
            category = AssetCategory.MARKETING_MENTAL_MODELS,
            content = """\
Replace vague claims with numbers and specifics.\n\nInstead of: \"We help teams move faster.\"\nUse: \"Teams ship 20–30% more features in the first quarter.\"\n\nSpecificity earns trust with analytical readers.""".trimIndent(),
            xpValue = 10
        )

        list += Asset(
            id = "book_3",
            title = "Essentialism – Trade-Offs",
            category = AssetCategory.BOOK_SUMMARIES,
            content = """\
You cannot do everything. Essentialism is the disciplined pursuit of less but better.\n\nAsk:\n- What is the single most important problem I can move today?\n- What will I say no to in order to protect it?\n\nTrade-offs are not failures; they are strategy.""".trimIndent(),
            xpValue = 10
        )

        list += Asset(
            id = "psychology_4",
            title = "Pre-Commitment Contract",
            category = AssetCategory.PSYCHOLOGY_TRICKS,
            content = """\
Lock in a future behavior by making the alternative costly or visible.\n\nExamples:\n- Share a clear deadline with a peer and schedule a check-in.\n- Use tools that block distractions during deep work windows.\n\nYou are designing the environment to support your intentions.""".trimIndent(),
            xpValue = 10
        )

        list += Asset(
            id = "memory_4",
            title = "Micro Teaching",
            category = AssetCategory.MEMORY_TECHNIQUES,
            content = """\
After learning a concept, explain it in 60 seconds as if to a busy colleague.\n\nRules:\n- No jargon unless necessary.\n- One concrete example.\n- One takeaway sentence.\n\nIf you cannot do this, you have located a gap in understanding.""".trimIndent(),
            xpValue = 10
        )

        list += Asset(
            id = "negotiation_4",
            title = "Agenda First",
            category = AssetCategory.NEGOTIATION_SCRIPTS,
            content = """\
Open important conversations with a short agenda.\n\nTemplate:\n- \"Here is what I would like to cover...\"\n- \"Is there anything you would add?\"\n\nThis sets expectations, surfaces hidden concerns, and makes you look prepared.""".trimIndent(),
            xpValue = 8
        )

        list += Asset(
            id = "marketing_4",
            title = "One-Line Positioning",
            category = AssetCategory.MARKETING_MENTAL_MODELS,
            content = """\
A simple frame: \"We help [who] achieve [result] without [unwanted cost].\"\n\nExample:\n- \"We help remote teams ship on time without calendar chaos.\"\n\nThis forces focus on a specific audience and outcome.""".trimIndent(),
            xpValue = 8
        )

        list += Asset(
            id = "book_4",
            title = "The One Thing – Focus Question",
            category = AssetCategory.BOOK_SUMMARIES,
            content = """\
The central question: \"What is the ONE thing I can do such that by doing it everything else will be easier or unnecessary?\"\n\nUse it to:
- Shape your day’s priority.\n- Decide what to protect in your calendar.\n\nAsk it whenever you feel pulled in too many directions.""".trimIndent(),
            xpValue = 10
        )

        return list
    }
}

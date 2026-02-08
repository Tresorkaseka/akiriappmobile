package com.example.akiriapp.data

import com.example.akiriapp.data.model.Course
import com.example.akiriapp.data.model.ForumPost
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Utility to seed Firestore with test data.
 */
class DataSeeder {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun seedCourses() {
        val courses = listOf(
            Course(
                title = "Développement Android avec Kotlin",
                description = "Maîtrisez le développement d'applications Android modernes en utilisant Kotlin et Jetpack Compose. Ce cours complet couvre tout, des bases aux fonctionnalités avancées.",
                price = 15000,
                instructorName = "Jean-Pierre Kabeya",
                category = "Programmation",
                rating = 4.8f,
                reviewCount = 124,
                duration = "12h 45min",
                lessonCount = 42,
                enrollmentCount = 350
            ),
            Course(
                title = "UI/UX Design pour Débutants",
                description = "Apprenez les principes fondamentaux du design d'interface et de l'expérience utilisateur. Créez des maquettes époustouflantes avec Figma.",
                price = 12000,
                instructorName = "Sarah Mulamba",
                category = "Design",
                rating = 4.6f,
                reviewCount = 89,
                duration = "8h 30min",
                lessonCount = 28,
                enrollmentCount = 210
            ),
            Course(
                title = "Marketing Digital 101",
                description = "Les stratégies essentielles pour promouvoir votre business en ligne. SEO, réseaux sociaux et publicité payante.",
                price = 10000,
                instructorName = "David Ngoie",
                category = "Marketing",
                rating = 4.5f,
                reviewCount = 56,
                duration = "6h 15min",
                lessonCount = 20,
                enrollmentCount = 180
            ),
            Course(
                title = "Entrepreneuriat en RDC",
                description = "Comment lancer et gérer une startup réussie dans le contexte congolais. Aspects juridiques, financiers et stratégiques.",
                price = 20000,
                instructorName = "Michel K.",
                category = "Business",
                rating = 4.9f,
                reviewCount = 215,
                duration = "15h 00min",
                lessonCount = 50,
                enrollmentCount = 500
            ),
            Course(
                title = "Anglais Professionnel",
                description = "Améliorez votre anglais pour le monde des affaires. Vocabulaire, grammaire et mises en situation.",
                price = 8000,
                instructorName = "Grace M.",
                category = "Langues",
                rating = 4.3f,
                reviewCount = 42,
                duration = "10h 00min",
                lessonCount = 35,
                enrollmentCount = 120
            )
        )

        val batch = firestore.batch()
        courses.forEach { course ->
            val docRef = firestore.collection("courses").document()
            batch.set(docRef, course.copy(id = docRef.id))
        }
        batch.commit().await()
    }

    suspend fun seedForumPosts(userId: String, userName: String) {
        val posts = listOf(
            ForumPost(
                authorId = userId,
                authorName = userName,
                title = "Quelle est la meilleure ressource pour apprendre Kotlin ?",
                content = "Salut à tous ! Je débute en développement Android et je cherche des recommandations pour apprendre Kotlin efficacement. Des idées ?",
                topic = "Programmation",
                tags = listOf("Débutant", "Kotlin"),
                likes = 12,
                commentCount = 5,
                createdAt = System.currentTimeMillis() - 86400000 // 1 day ago
            ),
            ForumPost(
                authorId = userId,
                authorName = userName,
                title = "Feedback sur mon dernier design",
                content = "J'ai créé une maquette pour une app de livraison. J'aimerais avoir vos avis sur l'utilisation des couleurs. Merci !",
                topic = "Design",
                tags = listOf("Feedback", "UI"),
                likes = 8,
                commentCount = 2,
                createdAt = System.currentTimeMillis() - 3600000 // 1 hour ago
            ),
            ForumPost(
                authorId = userId,
                authorName = userName,
                title = "Comment trouver ses premiers clients ?",
                content = "Je me lance en freelance et c'est un peu dur de trouver des contrats. Avez-vous des conseils ?",
                topic = "Business",
                tags = listOf("Freelance", "Conseils"),
                likes = 25,
                commentCount = 10,
                createdAt = System.currentTimeMillis() - 172800000 // 2 days ago
            )
        )

        val batch = firestore.batch()
        posts.forEach { post ->
            val docRef = firestore.collection("forum_posts").document()
            batch.set(docRef, post.copy(id = docRef.id))
        }
        batch.commit().await()
    }
}

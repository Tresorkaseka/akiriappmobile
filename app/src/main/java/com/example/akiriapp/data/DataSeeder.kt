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
                enrollmentCount = 350,
                thumbnailUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&q=80&w=1080"
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
                enrollmentCount = 210,
                thumbnailUrl = "https://images.unsplash.com/photo-1561070791-2526d30994b5?auto=format&fit=crop&q=80&w=1080"
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
                enrollmentCount = 180,
                thumbnailUrl = "https://images.unsplash.com/photo-1432888498266-38ffec3eaf0a?auto=format&fit=crop&q=80&w=1080"
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
                enrollmentCount = 500,
                thumbnailUrl = "https://images.unsplash.com/photo-1542744173-8e7e53415bb0?auto=format&fit=crop&q=80&w=1080"
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
                enrollmentCount = 120,
                thumbnailUrl = "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?auto=format&fit=crop&q=80&w=1080"
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

    suspend fun fixCurrentLessons() {
        // Topic-relevant YouTube videos per category (all verified public videos)
        val videosByCategory = mapOf(
            "Programmation" to listOf(
                "https://www.youtube.com/watch?v=F9UC9DY-vIU", // freeCodeCamp - Kotlin crash course
                "https://www.youtube.com/watch?v=EExSSotojVI", // Traversy - Kotlin in 1 hour
                "https://www.youtube.com/watch?v=hEFn72R3HN4", // Android dev basics
                "https://www.youtube.com/watch?v=BCSlZIUj18Y", // Android Development in Kotlin
                "https://www.youtube.com/watch?v=MxAgKCBrlXE"  // AppStuff - Kotlin coroutines
            ),
            "Design" to listOf(
                "https://www.youtube.com/watch?v=PeGfX7W1mJk", // Figma design tutorial
                "https://www.youtube.com/watch?v=IqnpDhcSNQg", // UI design principles
                "https://www.youtube.com/watch?v=c9Wg6Cb_YlU", // UX design basics
                "https://www.youtube.com/watch?v=68w2VwalD5w", // Figma from scratch
                "https://www.youtube.com/watch?v=AO_x1y1yFGQ"  // UX Design process
            ),
            "Marketing" to listOf(
                "https://www.youtube.com/watch?v=bixR-KIJKYM", // Marketing Digital intro
                "https://www.youtube.com/watch?v=mYBKVCnkYD0", // SEO tutorial
                "https://www.youtube.com/watch?v=o_wdkiQvLXY", // Social media strategy
                "https://www.youtube.com/watch?v=6-A4ULdSWKE", // Google Ads for beginners
                "https://www.youtube.com/watch?v=7VeqxeBlSzI"  // Digital marketing strategy
            ),
            "Business" to listOf(
                "https://www.youtube.com/watch?v=ulHbITX6XbE", // How to start a business
                "https://www.youtube.com/watch?v=0HHFNJqpndo", // Entrepreneurship 101
                "https://www.youtube.com/watch?v=f6aPdXa8fhI", // Business model canvas
                "https://www.youtube.com/watch?v=lDKLBGcGDss", // Startup secrets
                "https://www.youtube.com/watch?v=LMx3Dn4AJLY"  // How to run a successful business
            ),
            "Langues" to listOf(
                "https://www.youtube.com/watch?v=H6Nd7GqZuPk", // Business English conversation
                "https://www.youtube.com/watch?v=juKd26qkNAw", // Learn English professionally
                "https://www.youtube.com/watch?v=OjQdnQHt2Vo", // English for business
                "https://www.youtube.com/watch?v=g7n8_jkRjkA", // Speak English at work
                "https://www.youtube.com/watch?v=4VkiYjVNtSc"  // Business English phrases
            ),
            "Finance" to listOf(
                "https://www.youtube.com/watch?v=MrD_9j8OR-8", // Personal finance basics
                "https://www.youtube.com/watch?v=5vs1PqK9KdI", // Investment for beginners
                "https://www.youtube.com/watch?v=QJNFRonJmC4", // Financial literacy
                "https://www.youtube.com/watch?v=Kl2XFdBB7pk", // Budgeting tips
                "https://www.youtube.com/watch?v=xkG7UdBGaIw"  // How to manage money
            ),
            "Santé" to listOf(
                "https://www.youtube.com/watch?v=MBT2LmxLr4k", // Health tips
                "https://www.youtube.com/watch?v=sTANio_2E0Q", // Healthy lifestyle
                "https://www.youtube.com/watch?v=qH7b7TKkJjg", // Nutrition basics
                "https://www.youtube.com/watch?v=5Zc1gQa_q9k", // Mental health
                "https://www.youtube.com/watch?v=kJQP7kiw5Fk"  // Keeping active
            ),
            "default" to listOf(
                "https://www.youtube.com/watch?v=rfscVS0vtbw", // freeCodeCamp Python
                "https://www.youtube.com/watch?v=n_Dv4JMiwK8", // freeCodeCamp JS
                "https://www.youtube.com/watch?v=BHnMItX2hEQ", // future of programming
                "https://www.youtube.com/watch?v=EngW7tCbLp0", // Android dev
                "https://www.youtube.com/watch?v=jNQXAC9IVRw"  // first YouTube video
            )
        )

        try {
            // Step 1: Build a map of courseId -> category
            val courseSnapshot = firestore.collection("courses").get().await()
            val courseCategories = courseSnapshot.documents.associate { doc ->
                doc.id to (doc.getString("category") ?: "default")
            }

            // Step 2: Get all lessons
            val lessonSnapshot = firestore.collection("lessons").get().await()
            val batch = firestore.batch()
            var count = 0

            lessonSnapshot.documents.forEach { doc ->
                val courseId = doc.getString("courseId") ?: ""
                val category = courseCategories[courseId] ?: "default"
                val categoryVideos = videosByCategory[category] ?: videosByCategory["default"]!!
                val chosenUrl = categoryVideos.random()

                batch.update(doc.reference, "contentUrl", chosenUrl)
                count++

                if (count >= 490) {
                    batch.commit().await()
                    count = 0
                }
            }
            if (count > 0) {
                batch.commit().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fixMissingCourseImages() {
        val defaultImages = listOf(
            "https://images.unsplash.com/photo-1555066931-4365d14bab8c?auto=format&fit=crop&q=80&w=1080", // Code
            "https://images.unsplash.com/photo-1561070791-2526d30994b5?auto=format&fit=crop&q=80&w=1080", // UX
            "https://images.unsplash.com/photo-1432888498266-38ffec3eaf0a?auto=format&fit=crop&q=80&w=1080", // Marketing
            "https://images.unsplash.com/photo-1542744173-8e7e53415bb0?auto=format&fit=crop&q=80&w=1080", // Startup
            "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?auto=format&fit=crop&q=80&w=1080"  // English
        )
        
        try {
            val snapshot = firestore.collection("courses").get().await()
            val batch = firestore.batch()
            var count = 0

            snapshot.documents.forEach { doc ->
                val thumb = doc.getString("thumbnailUrl")
                if (thumb.isNullOrEmpty()) {
                    val randomImage = defaultImages.random()
                    batch.update(doc.reference, "thumbnailUrl", randomImage)
                    count++
                    
                    if (count >= 490) {
                        batch.commit().await()
                        count = 0
                    }
                }
            }
            if (count > 0) {
                batch.commit().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun seedLessonsForAllCourses() {
        // Curated, topic-matching lessons per category
        // Each category has a list of lessons: Triple(title, description, youtubeUrl)
        val lessonsByCategory = mapOf(
            "Programmation" to listOf(
                Triple("Introduction à Kotlin", "Découvrez les bases du langage Kotlin et son écosystème.", "https://www.youtube.com/watch?v=F9UC9DY-vIU"),
                Triple("Activités et Layouts Android", "Apprenez à construire vos premières écrans avec les Activités et Layouts.", "https://www.youtube.com/watch?v=EExSSotojVI"),
                Triple("Connexion à Firebase", "Intégrez Firebase Firestore à votre app Android en Kotlin.", "https://www.youtube.com/watch?v=hEFn72R3HN4"),
                Triple("Navigation et Architecture MVVM", "Maîtrisez la navigation entre écrans et le pattern MVVM.", "https://www.youtube.com/watch?v=BCSlZIUj18Y")
            ),
            "Design" to listOf(
                Triple("Introduction à Figma", "Prise en main de l'outil Figma et création de votre premier projet.", "https://www.youtube.com/watch?v=PeGfX7W1mJk"),
                Triple("Principes UX", "Les grands principes de l'expérience utilisateur appliqués au design.", "https://www.youtube.com/watch?v=IqnpDhcSNQg"),
                Triple("Créer un prototype interactif", "Créez un prototype cliquable avec Figma pour tester vos idées.", "https://www.youtube.com/watch?v=68w2VwalD5w"),
                Triple("Design System & Composants", "Organisez vos designs avec des composants réutilisables.", "https://www.youtube.com/watch?v=AO_x1y1yFGQ")
            ),
            "Marketing" to listOf(
                Triple("Introduction au Marketing Digital", "Les fondamentaux du marketing en ligne et les canaux à maîtriser.", "https://www.youtube.com/watch?v=bixR-KIJKYM"),
                Triple("SEO : Référencement Naturel", "Optimisez votre site pour apparaître en première page de Google.", "https://www.youtube.com/watch?v=mYBKVCnkYD0"),
                Triple("Réseaux Sociaux & Contenu", "Stratégies efficaces pour créer et distribuer votre contenu.", "https://www.youtube.com/watch?v=o_wdkiQvLXY"),
                Triple("Publicité Payante (Google Ads)", "Lancez vos premières campagnes publicitaires avec Google Ads.", "https://www.youtube.com/watch?v=6-A4ULdSWKE")
            ),
            "Business" to listOf(
                Triple("Lancer son Business", "Les étapes clés pour passer de l'idée au lancement de votre entreprise.", "https://www.youtube.com/watch?v=ulHbITX6XbE"),
                Triple("Entrepreneuriat 101", "Les bases de l'entrepreneuriat : mindset, obstacles et opportunités.", "https://www.youtube.com/watch?v=0HHFNJqpndo"),
                Triple("Modèle Economique (Business Model Canvas)", "Définissez votre modèle économique avec le Business Model Canvas.", "https://www.youtube.com/watch?v=f6aPdXa8fhI"),
                Triple("Gérer et Scaler son Entreprise", "Comment faire grandir votre startup et éviter les pièges courants.", "https://www.youtube.com/watch?v=lDKLBGcGDss")
            ),
            "Langues" to listOf(
                Triple("Business English : Les Bases", "Vocabulaire et expressions essentiels pour le monde professionnel.", "https://www.youtube.com/watch?v=H6Nd7GqZuPk"),
                Triple("Emails et Communication écrite", "Rédigez des emails professionnels clairs et efficaces en anglais.", "https://www.youtube.com/watch?v=juKd26qkNAw"),
                Triple("Conduire une Réunion en Anglais", "Phrases et techniques pour animer ou participer à une réunion en anglais.", "https://www.youtube.com/watch?v=OjQdnQHt2Vo"),
                Triple("Négociation et Présentations", "Comment présenter et négocier avec confiance en anglais.", "https://www.youtube.com/watch?v=g7n8_jkRjkA")
            ),
            "Finance" to listOf(
                Triple("Les Bases de la Finance Personnelle", "Gérez votre argent intelligemment avec les principes fondamentaux.", "https://www.youtube.com/watch?v=MrD_9j8OR-8"),
                Triple("Investir pour les Débutants", "Comment commencer à investir même avec un petit budget.", "https://www.youtube.com/watch?v=5vs1PqK9KdI"),
                Triple("Budget et Planification Financière", "Créez un budget réaliste et atteignez vos objectifs financiers.", "https://www.youtube.com/watch?v=QJNFRonJmC4"),
                Triple("Littératie Financière", "Comprendre les concepts financiers essentiels pour mieux décider.", "https://www.youtube.com/watch?v=Kl2XFdBB7pk")
            ),
            "Santé" to listOf(
                Triple("Mode de Vie Sain", "Les habitudes quotidiennes pour une meilleure santé physique et mentale.", "https://www.youtube.com/watch?v=MBT2LmxLr4k"),
                Triple("Nutrition Essentielle", "Comprendre les bases de la nutrition pour mieux manger.", "https://www.youtube.com/watch?v=sTANio_2E0Q"),
                Triple("Santé Mentale et Équilibre", "Techniques pour gérer le stress et maintenir un bon équilibre mental.", "https://www.youtube.com/watch?v=qH7b7TKkJjg"),
                Triple("Activité Physique au Quotidien", "Intégrez sport et mouvements dans votre routine de tous les jours.", "https://www.youtube.com/watch?v=5Zc1gQa_q9k")
            )
        )

        val defaultLessons = listOf(
            Triple("Introduction", "Découvrez les bases de ce cours.", "https://www.youtube.com/watch?v=rfscVS0vtbw"),
            Triple("Approfondissement", "Explorez les concepts plus avancés.", "https://www.youtube.com/watch?v=n_Dv4JMiwK8"),
            Triple("Pratique et Application", "Mettez en pratique ce que vous avez appris.", "https://www.youtube.com/watch?v=BHnMItX2hEQ"),
            Triple("Conclusion et Projet", "Résumé du cours et projet final.", "https://www.youtube.com/watch?v=EngW7tCbLp0")
        )

        try {
            val courseSnapshot = firestore.collection("courses").get().await()

            courseSnapshot.documents.forEach { courseDoc ->
                val courseId = courseDoc.id
                val category = courseDoc.getString("category") ?: "default"
                val courseTitle = courseDoc.getString("title") ?: "Cours"

                // Check if this course already has lessons
                val existingLessons = firestore.collection("lessons")
                    .whereEqualTo("courseId", courseId)
                    .get()
                    .await()

                if (!existingLessons.isEmpty) {
                    // Lessons exist, just fix their contentUrl
                    val batch = firestore.batch()
                    val categoryLessons = lessonsByCategory[category] ?: defaultLessons
                    existingLessons.documents.forEachIndexed { index, lessonDoc ->
                        val lessonTemplate = categoryLessons[index % categoryLessons.size]
                        batch.update(lessonDoc.reference, "contentUrl", lessonTemplate.third)
                    }
                    batch.commit().await()
                    return@forEach
                }

                // No lessons found - create 4 new lessons for this course
                val templateLessons = lessonsByCategory[category] ?: defaultLessons
                val batch = firestore.batch()

                templateLessons.forEachIndexed { index, (title, description, videoUrl) ->
                    val lessonRef = firestore.collection("lessons").document()
                    val lessonData = mapOf(
                        "id" to lessonRef.id,
                        "courseId" to courseId,
                        "title" to "${index + 1}. $title",
                        "description" to description,
                        "contentType" to "video",
                        "contentUrl" to videoUrl,
                        "order" to (index + 1),
                        "duration" to "${10 + index * 5}:00",
                        "createdAt" to System.currentTimeMillis() + index * 1000L
                    )
                    batch.set(lessonRef, lessonData)
                }

                // Update lesson count on course
                batch.update(
                    firestore.collection("courses").document(courseId),
                    "lessonCount", templateLessons.size
                )
                batch.commit().await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

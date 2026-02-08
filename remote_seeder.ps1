$apiKey = "AIzaSyCuDXm5GocnPsjWVUxP00HpQoXQmD09SSA"
$projectId = "akiri-app-ef455"
$authUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=$apiKey"

# 1. Create a temporary admin user to get an ID Token
$body = @{
    email = "admin-seeder-$((Get-Date).Ticks)@akiriapp.com"
    password = "seederPassword123!"
    returnSecureToken = $true
} | ConvertTo-Json

try {
    Write-Host "Authenticating..."
    $authResponse = Invoke-RestMethod -Uri $authUrl -Method Post -Body $body -ContentType "application/json"
    $idToken = $authResponse.idToken
    $localId = $authResponse.localId
    Write-Host "Authentication Successful! Token acquired."
} catch {
    Write-Host "Auth Failed: $_"
    exit 1
}

# 2. Define Firestore Endpoint
$firestoreBaseUrl = "https://firestore.googleapis.com/v1/projects/$projectId/databases/(default)/documents"

# 3. Helper function to create a document
function Add-FirestoreDocument {
    param (
        [string]$Collection,
        [hashtable]$Fields
    )
    
    $url = "$firestoreBaseUrl/$Collection"
    $headers = @{ "Authorization" = "Bearer $idToken" }
    
    $jsonFields = @{ fields = $Fields } | ConvertTo-Json -Depth 10
    
    try {
        Invoke-RestMethod -Uri $url -Method Post -Body $jsonFields -ContentType "application/json" -Headers $headers
        Write-Host "Created document in $Collection"
    } catch {
        Write-Host "Failed to create document in $Collection : $_"
        # Print detailed error if available
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            Write-Host $reader.ReadToEnd()
        }
    }
}

# 4. Create Sample Course 1
$course1 = @{
    title = @{ stringValue = "Développement Android Complet avec Kotlin" }
    description = @{ stringValue = "Apprenez à créer des applications Android modernes de A à Z avec ce cours complet." }
    price = @{ integerValue = 25000 }
    category = @{ stringValue = "Programmation" }
    instructorName = @{ stringValue = "Trésor Kaseka" }
    rating = @{ doubleValue = 4.8 }
    enrollmentCount = @{ integerValue = 120 }
    duration = @{ stringValue = "15h 30m" }
    createdAt = @{ integerValue = [int64]((Get-Date).ToUniversalTime() - (Get-Date "1/1/1970")).TotalMilliseconds }
}

Add-FirestoreDocument -Collection "courses" -Fields $course1

# 5. Create Sample Course 2
$course2 = @{
    title = @{ stringValue = "UI/UX Design Masterclass" }
    description = @{ stringValue = "Maîtrisez l'art du design d'interface et de l'expérience utilisateur." }
    price = @{ integerValue = 15000 }
    category = @{ stringValue = "Design" }
    instructorName = @{ stringValue = "Sarah Design" }
    rating = @{ doubleValue = 4.9 }
    enrollmentCount = @{ integerValue = 85 }
    duration = @{ stringValue = "10h 15m" }
    createdAt = @{ integerValue = [int64]((Get-Date).ToUniversalTime() - (Get-Date "1/1/1970")).TotalMilliseconds }
}

Add-FirestoreDocument -Collection "courses" -Fields $course2

# 6. Create Forum Post
$post1 = @{
    authorName = @{ stringValue = "Jean Étudiant" }
    content = @{ stringValue = "Ce cours est incroyable ! Merci beaucoup pour les explications claires." }
    likes = @{ integerValue = 12 }
    timestamp = @{ integerValue = [int64]((Get-Date).ToUniversalTime() - (Get-Date "1/1/1970")).TotalMilliseconds }
    topic = @{ stringValue = "Général" }
}

Add-FirestoreDocument -Collection "forum_posts" -Fields $post1

Write-Host "Seeding Completed!"

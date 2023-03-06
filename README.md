# Food recipes
I'm developing a web application here, which is a great way to practice my coding skills and create something I can share with others.

**Used libraries/tools**:
Scala 3, ZIO, Quill, Akka Typed Actor/Persistence, Caliban, Flyway, Docker, Docker Compose, Kubernetes, Kustomization, AWS

**Description**: the web app is linked to cooking recipes and helps users find inspiration for their next meal. It now includes features such as creating and filling out a recipe form, subscribing to updates, recipes submitted by users, searching by tags, ingredients, filtering by name or cooking time, downloading recipes, etc.

**Datamodel**: each recipe includes name, description, list of ingredients, tags (used for recipe categories such as breakfast, vegetarian), step-by-step instructions for cooking, time required.

**Authentication**: users need to get a token (go to http://localhost:9000/login/:yourlogin/:password) by giving a username and password (currently `test`) and then provide that token when they send requests: `"Authorization": "Bearer <yourToken>"`.

**Recipe submission**: users can submit their own recipes by editing the recipe form step by step if they can't fill in all the fields at once. They must also add all new ingredients and tags to the registers, if any. Go to http://localhost:9000/graphql
```
mutation add { addRecipeForm } #returns uuid

mutation update {
  updateNameInRecipeForm(
    id: "eabac448-1c93-436f-bd96-826a0e82b164",
    name: "soup"
  )
}

#fill other fields via mutations and save

mutation save {
  saveRecipeForm(id: "eabac448-1c93-436f-bd96-826a0e82b164")
}

query get {
  getRecipeForm(id: "eabac448-1c93-436f-bd96-826a0e82b164"){
    isSaved
    form {
      name
      description
      instructions
      preparationTimeMinutes
      waitingTimeMinutes
      tags
      ingredients {
        key
        value {
          amount: _1
          unit: _2
        }
      }
    }
  }
}
```


**Subscriptions**: users can receive notifications when a new recipe is added. I use websockets and ZIO Hub for this. Connect to ws://localhost:9000/ws/graphql and send messages:
```
{"type":"connection_init"}
{"type":"start", "payload": {"query": "subscription recipeUpdate { newRecipe { id name } }"}}
```

**Migrations**: you can add new database migrations to the `/resources/db/migration` folder; migrations written in Scala can also be applied if the location is specified (ex: `Flyway.configure().locations("default path", "new path")`)

**New features** that can be implemented:
- search functionality: use Elasticsearch to index recipes and more advanced searches
- recipe ratings and reviews: allow users to rate and review recipes to help others find the best ones
- personalized recommendations: use machine learning algorithms to suggest recipes to users based on their previous ratings and searches
- printed recipe cards: provide a printed version of each recipe for users who want to save it for later
- expanding the recipe model by adding nutritional information, image

##Startup and deployment
To run the application locally use `sudo docker-compose up` that will create two containers: `pg` for database and `api`. You can add changes to the file `docker-compose.yaml`.

Creating a Docker image:
- see `Dockerfile` and add changes if necessary
- specify configurations such as build image or exposed ports in `build.sbt`
- run `sbt docker:publishLocal` (for this `DockerPlugin` should be enabled) or you can publish to the remote repository

Deploying an application with Kubernetes (locally) using Kustomization:
- `k8s/kustomization.tmpl.yaml` -- Kubernetes manifest file that defines the resources needed to run the application, such as pods, services, and volumes. Using customization.
Create a Kubernetes cluster with a master node and one or more worker nodes - minikube will create the cluster and nodes for you.
Deploy the application using the kubectl command line tool and deployment/service objects or run the kustomization file.
(Optional) Scale the application with the kubectl command line tool by changing the number of running replicas
Go to localhost:9000/graphql





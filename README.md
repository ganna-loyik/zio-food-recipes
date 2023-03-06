# Food recipes
I'm developing a web application here, which is a great way to practice my coding skills and create something I can share with others.

**Used libraries/tools**:
- Scala 3
- ZIO
- Quill
- Akka Typed Actor/Persistence
- Caliban
- Flyway
- Docker, Docker Compose, 
- Kubernetes, Kustomization
- AWS

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

## Startup and deployment
To run the application locally use `sudo docker-compose up` that will create two containers: `pg` for database and `api`. You can add changes to the file `docker-compose.yaml`.

**Create a Docker image**:
- see `Dockerfile` and add changes if necessary
- alternatively, use `sbt docker:stage` after specifying configurations such as build image or exposed ports in `build.sbt`
- run `sbt docker:publishLocal` (for this `DockerPlugin` should be enabled); also you can publish to the remote repository

### Deploy an application with Kubernetes (locally) using Kustomization:
- `k8s/kustomization.tmpl.yaml` - defines the resources needed to run the application, such as volumes, services, etc. and generators for `ConfigMap` and `Secret`
- `k8s/api` - contains deployment and service for api. You should have local image named `zio-food-recipes`
- `k8s/db` - contains deployment, service and volume for postgres database
- install `minikube` to quickly setup a local `Kubernetes` cluster
- to deploy with `minikube` you need to make a few changes: in services replace type to `type: NodePort` and set `nodePort: 31000`; in database deployment use image `postgres:latest`; remove `images` key from kustomization file
- run commands in terminal:
```
minikube start
eval $(minikube docker-env)
```
- deploy the applicaion using the `kubectl` command line tool and deployment/service objects, or by using `Kustomization`:
```
kubectl apply -k k8s
kubectl get all
```
- go to localhost:9000/graphql

### Deploy to Amazon Elastic Container Service (ECS):
- create a Docker image and pust it to EC2
- create an `ECS` cluster using `AWS CLI`
- use `Amazon RDS` to set up a database with `PostgreSQL` engine, add database `recipes`:
- create an `ECS task definition` - a blueprint describing how to run your application. It includes information such as the Docker image used, CPU and memory requirements, port mapping, etc.
- use a custom security group, which allows connection to port 9000 from your IP (inbound rules)
- create an `ECS service`, which runs your task definition and manages its lifecycle
- go to the address given in ...

### Deploy to Amazon Elastic Kubernetes Service (EKS):
- create a Docker image of the app and pust it to `EC2`; use a public image for the database.
- create an `EKS` cluster, where `Kubernetes` workloads run
- TODO

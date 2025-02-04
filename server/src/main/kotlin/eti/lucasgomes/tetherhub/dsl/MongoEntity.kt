package eti.lucasgomes.tetherhub.dsl

@Target(AnnotationTarget.CLASS)
annotation class MongoEntity(val collectionName: String)
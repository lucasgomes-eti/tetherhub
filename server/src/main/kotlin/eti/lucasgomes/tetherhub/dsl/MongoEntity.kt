package eti.lucasgomes.tetherhub.dsl

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MongoEntity(val collectionName: String)
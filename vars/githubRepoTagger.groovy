#!/usr/bin/env groovy

def call(String tag, String credentials) {
  githubRepoTagger = new GithubRepoTagger(this)
  githubRepoTagger.tagWith(tag, credentials)
}

# setup process

## ruby 

    brew install ruby
    
check that the correct versions of `ruby` and `gem` are used:

    which ruby
    which gem
    
    ruby -v
    gem -v

## jekyll
    
    gem install jekyll bundler
    
    cd docs
    jekyll new .


## run locally

    bundle exec jekyll serve

 to make it look like the same on github, keep the gem up to date:
 
    bundle update github-pages
    
## view locally

    http://localhost:4000

## github

    https://pages.github.com/versions/


# Pdfy
Simple, lightweight, PDF viewer for android. Supports assets paths, or internet URLs.

[![](https://jitpack.io/v/boldijar/pdfy.svg)](https://jitpack.io/#boldijar/pdfy)

### What does it do?
It will load a PDF and show it in a custom view, it can be scrolled vertically and zoomed in or out.

### How can you open a PDF?
For now it only supports PDFs from assets or from a URL (using [okhttp](https://square.github.io/okhttp/) to download)

### How does it work?
* using PDFRenderer API from android
* download the pdf file and store it in local cache as PDF, and load pages while they are viewed
* show a recyclerview with all those images
* let the images zoom in and out using [Zoomage](https://github.com/jsibbold/zoomage)

# How to use the library

## Add it in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

## Add the dependency

```
dependencies {
    implementation 'com.github.boldijar:pdfy:<latest-version>'
}
```

Let the library know how to load the images. I didn't wanted to include this in the library because people might want to use their own caching library, you can chech the demo to see how to do this with [Glide](https://github.com/bumptech/glide).

```
class GlideLoader : PdfyImageLoader() {
    override fun loadImage(path: String, imageView: ImageView) {
        Glide.with(imageView).load(path).into(imageView)
    }
}
```

And set the loader before you use the library:
```
PdfyParser.init(GlideLoader())
```

Now load a PDF:

``` 
pdfyView.setPdf(
    path = "http://www.ignaciouriarte.com/works/18/pdfs/A100page47.pdf",
    type = PdfyType.FROM_INTERNET,
    uniqueCacheName = "<somePdfId>" // optional, used for caching, default is a random UUID
)
```

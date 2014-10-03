Infinite Fibonacci Scroller in Android
==============


# Simple Breadkown
This is a simple infinitely-scrollable Fibonacci sequence app.  I initially used a recursive Fibonacci algorithm but then determined that created a heavy bit of stack overhead and could lead to memory issues.  So I switched to iterative with some optimization for already-computed values.

I also was going to include a "pretty print" function that would add commas at the appropriate locations in the print-out, but that slowed down performance and created some unecessary overheaed.  Can add it in future.

# Basic Structure
* Main Acitivty (MainFib)
* Customized Adapter (FibAdapter)
* Customized ListView (FibListView)

MainFib also includes private ASyncTask for computing next set number of values in the sequence.

FibAdapter includes some optimizations for cutting down on creation of individual listview elements.

Can obviously add more as necessary.


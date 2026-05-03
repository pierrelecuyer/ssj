Speed tests and comparisons for RQMC point sets with 32-bit implementation.
See the program TestRQMCSpeed.java for the details. 
The same stream of random numbers is reused for all cases.

Speed test for n = 1048576, s = 10, and m = 10 replications.
------------------------------------------------------- 

Korobov lattice with random shift, implemented as an LCG 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.15625 seconds
Average = 0.4999995231628418

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      0.453125 seconds
Average = 0.5000000105680255

------------------------------------------------------- 

Rank-1 lattice with random shift 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.671875 seconds
Average = 0.5000000105678734

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      0.703125 seconds
Average = 0.5000000105678734

------------------------------------------------------- 

Rank-1 lattice with random shift, cached 

Time to randomize the points only: 
Total CPU time:      0.703125 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.921875 seconds
Average = 0.5000000105678734

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      1.125 seconds
Average = 0.5000000105678734

------------------------------------------------------- 

Rank-1 lattice with random shift + tent transformation 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.875 seconds
Average = 0.4999999999990261

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      1.375 seconds
Average = 0.4999999999990261

------------------------------------------------------- 

Sobol points with no randomization 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.25 seconds
Average = 0.4999995231628418

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      0.5 seconds
Average = 0.4999995231628418

------------------------------------------------------- 

Sobol points with random digital shift only 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.265625 seconds
Average = 0.5000000107306988

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      0.703125 seconds
Average = 0.5000000107306988

------------------------------------------------------- 

Sobol points with random digital shift + tent transformation 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.65625 seconds
Average = 0.5000000000000001

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      1.234375 seconds
Average = 0.5000000000000001

------------------------------------------------------- 

Sobol points with LMS + RDS 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.234375 seconds
Average = 0.5000000107306988

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      0.53125 seconds
Average = 0.5000000107306988

------------------------------------------------------- 

Sobol points with LMS + RDS, cached 

Time to randomize the points only: 
Total CPU time:      0.3125 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.515625 seconds
Average = 0.5000000107306988

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      1.015625 seconds
Average = 0.5000000107306988

------------------------------------------------------- 

Sobol points with NUS 

Time to randomize the points only: 
Total CPU time:      26.296875 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      25.640625 seconds
Average = 0.4999999996093197

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      27.15625 seconds
Average = 0.4999999996093197

------------------------------------------------------- 

Independent random points cached 

Time to randomize the points only: 
Total CPU time:      0.78125 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      1.015625 seconds
Average = 0.4999961790969174

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      1.421875 seconds
Average = 0.4999961790969174

------------------------------------------------------- 


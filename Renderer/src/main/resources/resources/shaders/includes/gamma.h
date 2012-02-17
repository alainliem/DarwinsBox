//<include includes/misc.h>
#ifndef GAMMA_VAL
    #define GAMMA_VAL 2.2
#endif
uconst vec3 gamma = vec3(GAMMA_VAL);

#ifdef COMPILED
    const vec3 invgamma = vec3(1. / gamma);
#else
    #define invgamma (1./gamma)
#endif

#ifdef GAMMA
    #define toLinear(x) pow(x, gamma)
    #define toGamma(x) pow(x, invgamma)
#else
    #define toLinear(x) x
    #define toGamma(x) x
#endif